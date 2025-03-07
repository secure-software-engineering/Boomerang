package boomerang.callgraph;

import boomerang.WeightedBoomerang;
import boomerang.scope.CallGraph;
import boomerang.scope.InvokeExpr;
import boomerang.scope.Method;
import boomerang.scope.Statement;
import java.util.Collection;

public interface ICallerCalleeResolutionStrategy {

  interface Factory {
    ICallerCalleeResolutionStrategy newInstance(WeightedBoomerang solver, CallGraph cg);
  }

  void computeFallback(ObservableDynamicICFG observableDynamicICFG);

  Method resolveSpecialInvoke(InvokeExpr ie);

  Collection<Method> resolveInstanceInvoke(Statement stmt);

  Method resolveStaticInvoke(InvokeExpr ie);
}
