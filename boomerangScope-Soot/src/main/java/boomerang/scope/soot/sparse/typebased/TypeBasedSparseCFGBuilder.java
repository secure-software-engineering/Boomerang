package boomerang.scope.soot.sparse.typebased;

import boomerang.scope.Val;
import boomerang.scope.soot.sparse.SootAdapter;
import boomerang.scope.soot.sparse.SparseCFGBuilder;
import boomerang.scope.soot.sparse.aliasaware.AliasAwareSparseCFGBuilder;
import com.google.common.graph.MutableGraph;
import java.util.*;
import java.util.logging.Logger;
import soot.*;
import soot.jimple.InvokeExpr;
import soot.jimple.Stmt;
import soot.jimple.internal.*;
import soot.toolkits.graph.BriefUnitGraph;
import soot.toolkits.graph.DirectedGraph;
import sparse.SparseAliasingCFG;
import sparse.eval.SparseCFGQueryLog;

public class TypeBasedSparseCFGBuilder extends SparseCFGBuilder {
  private static final Logger LOGGER = Logger.getLogger(AliasAwareSparseCFGBuilder.class.getName());

  private final boolean enableExceptions;
  private Deque<Type> typeWorklist;
  private Set<Type> containerTypes;

  public TypeBasedSparseCFGBuilder(boolean enableExceptions) {
    this.enableExceptions = enableExceptions;
  }

  public SparseAliasingCFG buildSparseCFG(
      Val queryVar, SootMethod m, Unit queryStmt, SparseCFGQueryLog queryLog) {
    typeWorklist = new ArrayDeque<>();
    containerTypes = new HashSet<>();
    DirectedGraph<Unit> unitGraph = new BriefUnitGraph(m.getActiveBody());

    Unit head = getHead(unitGraph);

    MutableGraph<Unit> mCFG = numberStmtsAndConvertToMutableGraph(unitGraph);

    int initialStmtCount = mCFG.nodes().size();

    //    LOGGER.info(m.getName() + " original");
    //    logCFG(LOGGER, mCFG);
    // if (m.getName().equals("id")) {
    Type typeOfQueryVar = SootAdapter.getTypeOfVal(queryVar);

    Set<Unit> stmtsToKeep = findStmtsToKeep(mCFG, head, typeOfQueryVar);

    containerTypes.add(typeOfQueryVar);
    while (!typeWorklist.isEmpty()) {
      Type containerType = typeWorklist.pop();
      stmtsToKeep.addAll(findStmtsToKeep(mCFG, head, containerType));
    }
    stmtsToKeep.add(queryStmt);
    List<Unit> tails = unitGraph.getTails();
    for (Unit tail : tails) {
      sparsify(mCFG, stmtsToKeep, head, tail);
    }

    //    LOGGER.info(m.getName() + " ta-sparse");
    //    logCFG(LOGGER, mCFG);
    // }
    int finalStmtCount = mCFG.nodes().size();
    queryLog.setInitialStmtCount(initialStmtCount);
    queryLog.setFinalStmtCount(finalStmtCount);

    return new SparseAliasingCFG(queryVar, mCFG, queryStmt, null, unitToNumber);
  }

  private void sparsify(MutableGraph<Unit> mCFG, Set<Unit> stmtsToKeep, Unit head, Unit tail) {
    Iterator<Unit> iter = getBFSIterator(mCFG, head);
    Set<Unit> stmsToRemove = new HashSet<>();
    while (iter.hasNext()) {
      Unit unit = iter.next();
      if (!isControlStmt(unit)
          && !stmtsToKeep.contains(unit)
          && !unit.equals(head)
          && !unit.equals(tail)) {
        stmsToRemove.add(unit);
      }
    }
    for (Unit unit : stmsToRemove) {
      removeStmt(mCFG, unit);
    }
  }

  private Set<Unit> findStmtsToKeep(MutableGraph<Unit> mCFG, Unit head, Type queryVarType) {
    Set<Unit> stmtsToKeep = new HashSet<>();
    Iterator<Unit> iter = getBFSIterator(mCFG, head);
    while (iter.hasNext()) {
      Unit unit = iter.next();
      if (keepStmt(unit, queryVarType)) {
        stmtsToKeep.add(unit);
      }
    }
    return stmtsToKeep;
  }

  private boolean isAssignableType(Type targetType, Type sourceType) {
    return Scene.v().getOrMakeFastHierarchy().canStoreType(targetType, sourceType)
        || Scene.v().getOrMakeFastHierarchy().canStoreType(sourceType, targetType);
  }

  private boolean keepStmt(Unit unit, Type queryVarType) {
    boolean keep =
        false; // Incase of Container.f = base.m(f_type), keep both base and Container as container
    // types
    if (unit instanceof Stmt) {
      Stmt stmt = (Stmt) unit;
      if (stmt.containsInvokeExpr()) {
        InvokeExpr invokeExpr = stmt.getInvokeExpr();
        List<Value> args = invokeExpr.getArgs();
        // v as arg
        for (Value arg : args) {
          if (isFieldTypeRelevant(queryVarType, arg)) {
            handleInvokeBase(invokeExpr, queryVarType);
            keep = true;
          }
          if (isAssignableType(arg.getType(), queryVarType)) {
            handleInvokeBase(invokeExpr, queryVarType);
            keep = true;
          }
        }
        // v as base v.m()
        if (isInvokeBase(queryVarType, invokeExpr)) {
          keep = true;
        }
      }
    }
    if (unit instanceof JAssignStmt) {
      JAssignStmt stmt = (JAssignStmt) unit;
      Value leftOp = stmt.getLeftOp();
      Value rightOp = stmt.getRightOp();

      // handle Casts (Not mentioned)
      if (rightOp instanceof JCastExpr) {
        JCastExpr cast = (JCastExpr) rightOp;
        rightOp = cast.getOp();
      }

      if (rightOp.toString().startsWith("this$")) { // TODO: quickfix
        keep = true;
      }

      // left's field is of the same type
      if (isFieldTypeRelevant(queryVarType, leftOp) || isFieldTypeRelevant(queryVarType, rightOp)) {
        keep = true;
      }

      // left or right of the same type
      if (isAssignableType(leftOp.getType(), queryVarType)
          || isAssignableType(rightOp.getType(), queryVarType)) {
        if (stmt.containsInvokeExpr()) {
          InvokeExpr invokeExpr = stmt.getInvokeExpr();
          // always check invoke base if its beign assigned to the type we track, because we need to
          // know how it was allocated
          if (invokeExpr instanceof AbstractInstanceInvokeExpr) {
            handleInvokeBase(invokeExpr, queryVarType);
          }
        }
        keep = true;
      }
    }
    return keep;
  }

  /**
   * To keep base of an invoke in the cfg
   *
   * @param invokeExpr
   * @param queryVarType
   */
  private void handleInvokeBase(InvokeExpr invokeExpr, Type queryVarType) {
    if (invokeExpr instanceof JVirtualInvokeExpr) {
      Value base = ((JVirtualInvokeExpr) invokeExpr).getBase();
      handleContainerType(base, queryVarType);
    } else if (invokeExpr instanceof JSpecialInvokeExpr) {
      Value base = ((JSpecialInvokeExpr) invokeExpr).getBase();
      handleContainerType(base, queryVarType);
    }
  }

  private void handleContainerType(Value base, Type queryVarType) {
    if (!base.getType().equals(queryVarType)
        && !containerTypes.contains(base.getType())) { // we already track this type
      typeWorklist.push(base.getType()); // we need to find how base is allocated
      containerTypes.add(base.getType()); // we will track all the bases where queryType is a field
    }
  }

  /**
   * value is an instance field. The field is the same type as queryVarType. Keep the base type of
   * that field.
   *
   * @param queryVarType
   * @param value
   * @return
   */
  private boolean isFieldTypeRelevant(Type queryVarType, Value value) {
    // left's field is of the same type
    if (value instanceof JInstanceFieldRef) { // TODO: rightOp case?
      JInstanceFieldRef fieldRef = (JInstanceFieldRef) value;
      SootField field = fieldRef.getField();
      if (isAssignableType(field.getType(), queryVarType)) {
        Value base = fieldRef.getBase();
        handleContainerType(base, queryVarType);
        return true;
      }
    }
    return false;
  }

  private boolean isInvokeBase(Type queryVarType, InvokeExpr invokeExpr) {
    if (invokeExpr instanceof JVirtualInvokeExpr) {
      Value base = ((JVirtualInvokeExpr) invokeExpr).getBase();
      return isAssignableType(base.getType(), queryVarType);
    } else if (invokeExpr instanceof JSpecialInvokeExpr) {
      Value base = ((JSpecialInvokeExpr) invokeExpr).getBase();
      return isAssignableType(base.getType(), queryVarType);
    }
    return false;
  }
}
