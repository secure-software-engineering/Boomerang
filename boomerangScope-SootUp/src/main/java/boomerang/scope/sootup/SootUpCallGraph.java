package boomerang.scope.sootup;

import boomerang.scope.CallGraph;
import boomerang.scope.Statement;
import boomerang.scope.sootup.jimple.JimpleUpMethod;
import boomerang.scope.sootup.jimple.JimpleUpStatement;
import java.util.Collection;
import java.util.Optional;
import sootup.core.jimple.common.stmt.InvokableStmt;
import sootup.core.signatures.MethodSignature;
import sootup.java.core.JavaSootMethod;

public class SootUpCallGraph extends CallGraph {

  public SootUpCallGraph(
      sootup.callgraph.CallGraph callGraph, Collection<JavaSootMethod> entryPoints) {

    assert !callGraph.getMethodSignatures().isEmpty();
    assert !entryPoints.isEmpty();

    // TODO: add a convenience method for this(edge collecting) to sootup
    callGraph.getMethodSignatures().stream()
        .flatMap((MethodSignature methodSignature) -> callGraph.callsTo(methodSignature).stream())
        .forEach(
            call -> {
              Optional<JavaSootMethod> sourceOpt =
                  SootUpFrameworkScope.getInstance().getSootMethod(call.getSourceMethodSignature());
              Optional<JavaSootMethod> targetOpt =
                  SootUpFrameworkScope.getInstance().getSootMethod(call.getTargetMethodSignature());

              if (sourceOpt.isEmpty() || targetOpt.isEmpty()) {
                return;
              }

              JavaSootMethod sourceMethod = sourceOpt.get();
              JavaSootMethod targetMethod = targetOpt.get();
              if (!sourceMethod.hasBody() || !targetMethod.hasBody()) {
                return;
              }

              InvokableStmt invokableStmt = call.getInvokableStmt();
              if (!invokableStmt.containsInvokeExpr()) {
                return;
              }

              Statement callSite =
                  JimpleUpStatement.create(invokableStmt, JimpleUpMethod.of(sourceMethod));
              this.addEdge(new Edge(callSite, JimpleUpMethod.of(targetMethod)));

              LOGGER.trace("Added edge {} -> {}", callSite, targetMethod);
            });

    for (JavaSootMethod m : entryPoints) {
      if (m.hasBody()) {
        this.addEntryPoint(JimpleUpMethod.of(m));
        LOGGER.trace("Added entry point: {}", m);
      }
    }

    if (getEdges().isEmpty()) {
      throw new IllegalStateException("CallGraph is empty!");
    }
  }
}
