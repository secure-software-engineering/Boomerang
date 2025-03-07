package boomerang.scope.soot;

import boomerang.scope.CallGraph;
import boomerang.scope.Method;
import boomerang.scope.Statement;
import boomerang.scope.soot.jimple.JimpleMethod;
import boomerang.scope.soot.jimple.JimplePhantomMethod;
import boomerang.scope.soot.jimple.JimpleStatement;
import java.util.Collection;
import soot.SootMethod;

public class SootCallGraph extends CallGraph {

  public SootCallGraph(
      soot.jimple.toolkits.callgraph.CallGraph callGraph, Collection<SootMethod> entryPoints) {
    for (soot.jimple.toolkits.callgraph.Edge e : callGraph) {
      if (!e.src().hasActiveBody() || e.srcStmt() == null) {
        continue;
      }

      Statement callSite = JimpleStatement.create(e.srcStmt(), JimpleMethod.of(e.src()));
      if (callSite.containsInvokeExpr()) {
        // Distinguish between loaded methods and phantom methods to cover all existing edges
        Method target;
        if (e.tgt().hasActiveBody()) {
          target = JimpleMethod.of(e.tgt());
        } else {
          target = JimplePhantomMethod.of(e.tgt());
        }

        LOGGER.trace("Call edge from {} to target method {}", callSite, e.tgt());
        this.addEdge(new Edge(callSite, target));
      }
    }

    for (SootMethod m : entryPoints) {
      if (m.hasActiveBody()) {
        this.addEntryPoint(JimpleMethod.of(m));
        LOGGER.trace("Added entry point: {}", m);
      }
    }

    if (getEdges().isEmpty()) {
      throw new IllegalStateException("CallGraph is empty!");
    }
  }
}
