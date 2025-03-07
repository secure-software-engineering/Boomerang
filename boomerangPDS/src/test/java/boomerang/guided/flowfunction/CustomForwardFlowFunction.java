package boomerang.guided.flowfunction;

import boomerang.ForwardQuery;
import boomerang.flowfunction.DefaultForwardFlowFunction;
import boomerang.flowfunction.DefaultForwardFlowFunctionOptions;
import boomerang.scope.ControlFlowGraph.Edge;
import boomerang.scope.DeclaredMethod;
import boomerang.scope.Method;
import boomerang.scope.Statement;
import boomerang.scope.Val;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import wpds.interfaces.State;

public class CustomForwardFlowFunction extends DefaultForwardFlowFunction {

  public CustomForwardFlowFunction(DefaultForwardFlowFunctionOptions options) {
    super(options);
  }

  @Override
  public Collection<State> callToReturnFlow(ForwardQuery query, Edge edge, Val fact) {
    if (edge.getStart().containsInvokeExpr()) {
      // Avoid any propagations by passing the call site.
      if (declaredMethodIsSystemExit(edge.getStart())) {
        return Collections.emptySet();
      }
    }
    return super.callToReturnFlow(query, edge, fact);
  }

  @Override
  public Set<State> normalFlow(ForwardQuery query, Edge nextEdge, Val fact) {
    if (nextEdge.getStart().containsInvokeExpr()) {
      // Avoid any propagations by passing any call site (this covers the case, when the fact is not
      // used at the call site).
      if (declaredMethodIsSystemExit(nextEdge.getStart())) {
        return Collections.emptySet();
      }
    }
    return super.normalFlow(query, nextEdge, fact);
  }

  public boolean declaredMethodIsSystemExit(Statement callSite) {
    DeclaredMethod method = callSite.getInvokeExpr().getMethod();
    return method.getDeclaringClass().getFullyQualifiedName().equals("java.lang.System")
        && method.getName().equals("exit");
  }

  @Override
  public Set<Val> callFlow(Statement callSite, Val fact, Method callee) {
    // Avoid propagations into the method when a call parameter reaches the call site
    if (callee.getDeclaringClass().getFullyQualifiedName().equals("java.lang.System")
        && callee.getName().equals("exit")) {
      return Collections.emptySet();
    }
    return super.callFlow(callSite, fact, callee);
  }
}
