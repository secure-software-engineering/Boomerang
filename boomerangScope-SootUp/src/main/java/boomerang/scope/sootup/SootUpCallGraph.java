/**
 * ***************************************************************************** 
 * Copyright (c) 2018 Fraunhofer IEM, Paderborn, Germany
 * <p>
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * <p>
 * SPDX-License-Identifier: EPL-2.0
 * <p>
 * Contributors:
 *   Johannes Spaeth - initial API and implementation
 * *****************************************************************************
 */
package boomerang.scope.sootup;

import boomerang.scope.CallGraph;
import boomerang.scope.Statement;
import boomerang.scope.sootup.jimple.JimpleUpMethod;
import boomerang.scope.sootup.jimple.JimpleUpPhantomMethod;
import boomerang.scope.sootup.jimple.JimpleUpStatement;
import java.util.Collection;
import java.util.Optional;
import sootup.core.jimple.common.expr.AbstractInvokeExpr;
import sootup.core.jimple.common.expr.JStaticInvokeExpr;
import sootup.core.jimple.common.stmt.InvokableStmt;
import sootup.core.signatures.MethodSignature;
import sootup.java.core.JavaSootMethod;
import sootup.java.core.views.JavaView;

public class SootUpCallGraph extends CallGraph {

  public SootUpCallGraph(
      JavaView view, sootup.callgraph.CallGraph callGraph, Collection<JavaSootMethod> entryPoints) {

    assert !callGraph.getMethodSignatures().isEmpty();
    assert !entryPoints.isEmpty();

    // TODO: add a convenience method for this(edge collecting) to sootup
    callGraph.getMethodSignatures().stream()
        .flatMap((MethodSignature methodSignature) -> callGraph.callsTo(methodSignature).stream())
        .forEach(
            call -> {
              Optional<JavaSootMethod> sourceOpt = view.getMethod(call.getSourceMethodSignature());
              if (sourceOpt.isEmpty()) {
                return;
              }

              JavaSootMethod sourceMethod = sourceOpt.get();
              if (!sourceMethod.hasBody()) {
                return;
              }

              InvokableStmt invokableStmt = call.getInvokableStmt();
              if (!invokableStmt.containsInvokeExpr()) {
                return;
              }

              Statement callSite =
                  JimpleUpStatement.create(invokableStmt, JimpleUpMethod.of(sourceMethod, view));

              MethodSignature targetSig = call.getTargetMethodSignature();
              Optional<JavaSootMethod> targetOpt = view.getMethod(targetSig);

              Optional<AbstractInvokeExpr> invokeExprOpt = invokableStmt.getInvokeExpr();
              if (invokeExprOpt.isEmpty()) {
                return;
              }

              boolean isStaticInvokeExpr = invokeExprOpt.get() instanceof JStaticInvokeExpr;
              if (targetOpt.isPresent()) {
                if (targetOpt.get().hasBody()) {
                  this.addEdge(new Edge(callSite, JimpleUpMethod.of(targetOpt.get(), view)));
                } else {
                  this.addEdge(
                      new Edge(
                          callSite, JimpleUpPhantomMethod.of(targetSig, view, isStaticInvokeExpr)));
                }
              } else {
                this.addEdge(
                    new Edge(
                        callSite, JimpleUpPhantomMethod.of(targetSig, view, isStaticInvokeExpr)));
              }

              LOGGER.trace("Added edge {} -> {}", callSite, targetSig);
            });

    for (JavaSootMethod m : entryPoints) {
      if (m.hasBody()) {
        this.addEntryPoint(JimpleUpMethod.of(m, view));
        LOGGER.trace("Added entry point: {}", m);
      }
    }

    if (getEdges().isEmpty() && entryPoints.isEmpty()) {
      throw new IllegalStateException("CallGraph is empty!");
    }
  }
}
