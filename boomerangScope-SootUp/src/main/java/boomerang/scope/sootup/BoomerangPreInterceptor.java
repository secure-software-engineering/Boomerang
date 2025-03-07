package boomerang.scope.sootup;

import java.util.*;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import sootup.core.graph.MutableStmtGraph;
import sootup.core.jimple.Jimple;
import sootup.core.jimple.basic.*;
import sootup.core.jimple.common.constant.ClassConstant;
import sootup.core.jimple.common.constant.Constant;
import sootup.core.jimple.common.expr.AbstractInstanceInvokeExpr;
import sootup.core.jimple.common.expr.AbstractInvokeExpr;
import sootup.core.jimple.common.expr.JStaticInvokeExpr;
import sootup.core.jimple.common.ref.JArrayRef;
import sootup.core.jimple.common.ref.JInstanceFieldRef;
import sootup.core.jimple.common.ref.JStaticFieldRef;
import sootup.core.jimple.common.stmt.*;
import sootup.core.model.Body;
import sootup.core.transform.BodyInterceptor;
import sootup.core.views.View;

public class BoomerangPreInterceptor implements BodyInterceptor {

  private final boolean TRANSFORM_CONSTANTS_SETTINGS;

  private static final String LABEL = "varReplacer";
  private int replaceCounter = 0;

  public BoomerangPreInterceptor() {
    this(true);
  }

  public BoomerangPreInterceptor(boolean transformConstantsSettings) {
    TRANSFORM_CONSTANTS_SETTINGS = transformConstantsSettings;
  }

  @Override
  public void interceptBody(@Nonnull Body.BodyBuilder bodyBuilder, @Nonnull View view) {
    addNopStatementsToMethod(bodyBuilder);

    if (TRANSFORM_CONSTANTS_SETTINGS) {
      transformConstantsAtFieldWrites(bodyBuilder);
    }
  }

  private void addNopStatementsToMethod(Body.BodyBuilder body) {
    // Initial nop statement
    JNopStmt initialNop = Jimple.newNopStmt(StmtPositionInfo.getNoStmtPositionInfo());
    MutableStmtGraph stmtGraph = body.getStmtGraph();
    stmtGraph.insertBefore(stmtGraph.getStartingStmt(), initialNop);

    // Collect if-statements
    Collection<JIfStmt> ifStatements =
        stmtGraph.getNodes().stream()
            .filter(stmt -> stmt instanceof JIfStmt)
            .map(stmt -> (JIfStmt) stmt)
            .collect(Collectors.toSet());

    // Add nop statement before each if-statement branch
    for (JIfStmt ifStmt : ifStatements) {
      // Branch under if-Statement: Insert after if-statement
      List<Stmt> successors = stmtGraph.successors(ifStmt);
      if (!successors.isEmpty()) {
        Stmt fallsThroughSuccessor = successors.get(JIfStmt.FALSE_BRANCH_IDX);

        JNopStmt nopAfterIfStmt = Jimple.newNopStmt(ifStmt.getPositionInfo());
        stmtGraph.insertBefore(fallsThroughSuccessor, nopAfterIfStmt);
      }

      // Branch for target: For if-statements, there is always exactly one target
      Stmt target = successors.get(JIfStmt.TRUE_BRANCH_IDX);
      JNopStmt nopBeforeTarget = Jimple.newNopStmt(target.getPositionInfo());
      stmtGraph.insertBefore(target, nopBeforeTarget);
    }
  }

  private void transformConstantsAtFieldWrites(Body.BodyBuilder body) {
    Collection<Stmt> stmtsWithConstants = getStatementsWithConstants(body);
    for (Stmt stmt : stmtsWithConstants) {
      if (stmt instanceof JAssignStmt) {
        /* Transform simple assignments to two assignment steps:
         * - value = 10;
         * becomes
         * - varReplacer = 10;
         * - value = varReplacer;
         */
        JAssignStmt assignStmt = (JAssignStmt) stmt;

        LValue leftOp = assignStmt.getLeftOp();
        Value rightOp = assignStmt.getRightOp();

        if (isFieldRef(leftOp)
            && rightOp instanceof Constant
            && !(rightOp instanceof ClassConstant)) {
          String label = LABEL + replaceCounter++;
          Local local = Jimple.newLocal(label, rightOp.getType());
          JAssignStmt newAssignStmt = Jimple.newAssignStmt(local, rightOp, stmt.getPositionInfo());

          body.addLocal(local);
          body.getStmtGraph().insertBefore(stmt, newAssignStmt);

          JAssignStmt updatedAssignStmt =
              Jimple.newAssignStmt(leftOp, local, stmt.getPositionInfo());
          body.getStmtGraph().replaceNode(stmt, updatedAssignStmt);
        }
      }

      if (stmt.isInvokableStmt()) {
        /* Extract constant arguments to new assignments
         * - method(10)
         * becomes
         * - varReplacer = 10
         * - method(varReplacer)
         */

        InvokableStmt invStmt = stmt.asInvokableStmt();
        Optional<AbstractInvokeExpr> InvokeExprOpt = invStmt.getInvokeExpr();

        if (InvokeExprOpt.isPresent()) {
          // TODO: ms: dont use in production?!
          if (!stmt.toString().contains("test.assertions.Assertions:")
              && !stmt.toString().contains("intQueryFor")) {

            List<Immediate> newArgs = new ArrayList<>();
            AbstractInvokeExpr invokeExpr = InvokeExprOpt.get();
            for (int i = 0; i < invokeExpr.getArgCount(); i++) {
              Immediate arg = invokeExpr.getArg(i);

              if (arg instanceof Constant && !(arg instanceof ClassConstant)) {
                String label = LABEL + replaceCounter++;
                Local paramLocal = Jimple.newLocal(label, arg.getType());
                JAssignStmt newAssignStmt =
                    Jimple.newAssignStmt(paramLocal, arg, stmt.getPositionInfo());

                body.addLocal(paramLocal);
                body.getStmtGraph().insertBefore(stmt, newAssignStmt);
                newArgs.add(paramLocal);
              } else {
                newArgs.add(arg);
              }
            }

            // Update the invoke expression with new arguments
            AbstractInvokeExpr newInvokeExpr;
            if (invokeExpr instanceof JStaticInvokeExpr) {
              newInvokeExpr = ((JStaticInvokeExpr) invokeExpr).withArgs(newArgs);
            } else if (invokeExpr instanceof AbstractInstanceInvokeExpr) {
              newInvokeExpr = ((AbstractInstanceInvokeExpr) invokeExpr).withArgs(newArgs);
            } else {
              throw new IllegalStateException("unknown InvokeExpr.");
            }

            if (stmt instanceof JInvokeStmt) {
              JInvokeStmt newStmt = ((JInvokeStmt) stmt).withInvokeExpr(newInvokeExpr);
              body.getStmtGraph().replaceNode(stmt, newStmt);
            } else if (stmt instanceof JAssignStmt) {
              JAssignStmt newStmt = ((JAssignStmt) stmt).withRValue(newInvokeExpr);
              body.getStmtGraph().replaceNode(stmt, newStmt);
            }
          }
        }
      }

      if (stmt instanceof JReturnStmt) {
        /* Transform return stmtsWithConstants into two stmtsWithConstants
         * - return 10
         * becomes
         * - varReplacer = 10
         * - return varReplacer
         */
        JReturnStmt returnStmt = (JReturnStmt) stmt;

        String label = LABEL + replaceCounter++;
        Local local = Jimple.newLocal(label, returnStmt.getOp().getType());
        JAssignStmt assignStmt =
            Jimple.newAssignStmt(local, returnStmt.getOp(), returnStmt.getPositionInfo());

        body.addLocal(local);
        body.getStmtGraph().insertBefore(stmt, assignStmt);

        JReturnStmt newReturnStmt = Jimple.newReturnStmt(local, returnStmt.getPositionInfo());
        body.getStmtGraph().replaceNode(stmt, newReturnStmt);
      }
    }
  }

  private Collection<Stmt> getStatementsWithConstants(Body.BodyBuilder body) {
    Collection<Stmt> result = new HashSet<>();

    // Assign statements: If the right side is a constant
    // Consider arguments of invoke expressions
    // Check for constant return values
    body.getStmts()
        .forEach(
            stmt -> {
              if (stmt instanceof JAssignStmt) {
                JAssignStmt assignStmt = (JAssignStmt) stmt;

                if (isFieldRef(assignStmt.getLeftOp())
                    && assignStmt.getRightOp() instanceof Constant) {
                  result.add(stmt);
                }
              }
              if (stmt.isInvokableStmt()) {
                InvokableStmt invokableStmt = stmt.asInvokableStmt();
                if (invokableStmt.containsInvokeExpr()) {
                  Optional<AbstractInvokeExpr> invokeExpr = invokableStmt.getInvokeExpr();
                  if (invokeExpr.isPresent()) {
                    for (Value arg : invokeExpr.get().getArgs()) {
                      if (arg instanceof Constant) {
                        result.add(stmt);
                      }
                    }
                  }
                }
              }
              if (stmt instanceof JReturnStmt) {
                JReturnStmt returnStmt = (JReturnStmt) stmt;
                if (returnStmt.getOp() instanceof Constant) {
                  result.add(stmt);
                }
              }
            });

    return result;
  }

  private boolean isFieldRef(Value value) {
    return value instanceof JInstanceFieldRef
        || value instanceof JStaticFieldRef
        || value instanceof JArrayRef;
  }
}
