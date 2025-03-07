package boomerang.options;

import boomerang.scope.AllocVal;
import boomerang.scope.Method;
import boomerang.scope.Statement;
import boomerang.scope.Val;
import java.util.Optional;

public class DefaultAllocationSite implements IAllocationSite {

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

    // Basic constants: var = <constant>
    if (rightOp.isConstant()) {
      return Optional.of(new AllocVal(leftOp, statement, rightOp));
    }

    // Null assignments: var = null
    if (rightOp.isNull()) {
      return Optional.of(new AllocVal(leftOp, statement, rightOp));
    }

    // Array assignments: var = new arr[]
    if (rightOp.isArrayAllocationVal()) {
      return Optional.of(new AllocVal(leftOp, statement, rightOp));
    }

    // New object creations: var = new Object;
    if (rightOp.isNewExpr()) {
      return Optional.of(new AllocVal(leftOp, statement, rightOp));
    }

    return Optional.empty();
  }
}
