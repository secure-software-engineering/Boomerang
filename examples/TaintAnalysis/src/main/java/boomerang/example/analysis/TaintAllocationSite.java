package boomerang.example.analysis;

import boomerang.options.IAllocationSite;
import boomerang.scope.AllocVal;
import boomerang.scope.DeclaredMethod;
import boomerang.scope.Method;
import boomerang.scope.Statement;
import boomerang.scope.Val;
import boomerang.utils.MethodWrapper;

import java.util.Collection;
import java.util.Optional;

public class TaintAllocationSite implements IAllocationSite {

  private final Collection<MethodWrapper> sources;

  public TaintAllocationSite(Collection<MethodWrapper> sources) {
    this.sources = sources;
  }

  @Override
  public Optional<AllocVal> getAllocationSite(Method method, Statement statement, Val val) {
    if (!statement.isAssignStmt()) {
      return Optional.empty();
    }

    Val leftOp = statement.getLeftOp();
    Val rightOp = statement.getRightOp();

    if (!leftOp.equals(val)) {
      return Optional.empty();
    }

    if (statement.containsInvokeExpr()) {
      DeclaredMethod declaredMethod = statement.getInvokeExpr().getDeclaredMethod();

      if (sources.contains(declaredMethod.toMethodWrapper())) {
        AllocVal allocVal = new AllocVal(leftOp, statement, rightOp);

        return Optional.of(allocVal);
      }
    }

    return Optional.empty();
  }
}
