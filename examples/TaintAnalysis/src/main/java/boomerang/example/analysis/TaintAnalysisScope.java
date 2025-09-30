package boomerang.example.analysis;

import boomerang.BackwardQuery;
import boomerang.Query;
import boomerang.scope.AnalysisScope;
import boomerang.scope.ControlFlowGraph;
import boomerang.scope.DeclaredMethod;
import boomerang.scope.FrameworkScope;
import boomerang.scope.InvokeExpr;
import boomerang.scope.Statement;
import boomerang.scope.Val;
import boomerang.utils.MethodWrapper;

import java.util.Collection;
import java.util.Collections;

public class TaintAnalysisScope extends AnalysisScope {

  private final Collection<MethodWrapper> sinks;

  public TaintAnalysisScope(FrameworkScope frameworkScope, Collection<MethodWrapper> sinks) {
    super(frameworkScope);

    this.sinks = sinks;
  }

  @Override
  protected Collection<? extends Query> generate(ControlFlowGraph.Edge edge) {
    Statement statement = edge.getTarget();

    if (statement.containsInvokeExpr()) {
      InvokeExpr invokeExpr = statement.getInvokeExpr();
      DeclaredMethod declaredMethod = invokeExpr.getDeclaredMethod();

      if (sinks.contains(declaredMethod.toMethodWrapper())) {
        Val arg = invokeExpr.getArg(0);

        BackwardQuery query = BackwardQuery.make(edge, arg);
        return Collections.singleton(query);
      }
    }

    return Collections.emptySet();
  }
}
