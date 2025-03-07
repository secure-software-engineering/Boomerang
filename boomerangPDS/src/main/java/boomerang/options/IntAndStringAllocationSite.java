package boomerang.options;

import boomerang.scope.AllocVal;
import boomerang.scope.DeclaredMethod;
import boomerang.scope.Method;
import boomerang.scope.Statement;
import boomerang.scope.Val;
import java.util.Optional;

public class IntAndStringAllocationSite extends DefaultAllocationSite {

  @Override
  public Optional<AllocVal> getAllocationSite(Method method, Statement statement, Val fact) {
    if (!statement.isAssignStmt()) {
      return Optional.empty();
    }

    Val leftOp = statement.getLeftOp();
    Val rightOp = statement.getRightOp();
    if (!leftOp.equals(fact)) {
      return Optional.empty();
    }

    // Length expressions: var = arr.length
    if (rightOp.isLengthExpr()) {
      return Optional.of(new AllocVal(leftOp, statement, rightOp));
    }

    // BigInteger.valueOf(x) -> allocation site is x
    if (statement.containsInvokeExpr()) {
      DeclaredMethod declaredMethod = statement.getInvokeExpr().getMethod();

      if (declaredMethod
          .toString()
          .equals("<java.math.BigInteger: java.math.BigInteger valueOf(long)>")) {
        Val arg = statement.getInvokeExpr().getArg(0);
        return Optional.of(new AllocVal(leftOp, statement, arg));
      }
    }

    return super.getAllocationSite(method, statement, fact);
  }
}
