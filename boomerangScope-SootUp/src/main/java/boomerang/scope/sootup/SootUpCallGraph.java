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

import boomerang.scope.LazyCallGraph;
import boomerang.scope.Method;
import boomerang.scope.Statement;
import boomerang.scope.sootup.jimple.JimpleUpMethod;
import boomerang.scope.sootup.jimple.JimpleUpPhantomMethod;
import boomerang.scope.sootup.jimple.JimpleUpStatement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import sootup.callgraph.CallGraph.Call;
import sootup.core.jimple.common.expr.AbstractInvokeExpr;
import sootup.core.jimple.common.expr.JStaticInvokeExpr;
import sootup.core.jimple.common.stmt.InvokableStmt;
import sootup.core.signatures.MethodSignature;
import sootup.java.core.JavaSootMethod;
import sootup.java.core.views.JavaView;

/** Forwards to SootUp's call graph, which is kept alive by this object. */
public class SootUpCallGraph extends LazyCallGraph {

  private final JavaView view;
  private final sootup.callgraph.CallGraph callGraph;
  private final List<JavaSootMethod> entryPoints;

  public SootUpCallGraph(
      JavaView view, sootup.callgraph.CallGraph callGraph, Collection<JavaSootMethod> entryPoints) {

    assert !callGraph.getMethodSignatures().isEmpty();
    assert !entryPoints.isEmpty();

    this.view = view;
    this.callGraph = callGraph;
    this.entryPoints = new ArrayList<>(entryPoints);

    if (callGraph.callCount() == 0 && entryPoints.isEmpty()) {
      throw new IllegalStateException("CallGraph is empty!");
    }
  }

  /** The source method of {@code call} if it has a body and the call site an invoke expression. */
  private Optional<JavaSootMethod> validSource(Call call) {
    if (call.invokableStmt().getInvokeExpr().isEmpty()) {
      return Optional.empty();
    }
    return view.getMethod(call.sourceMethodSignature()).filter(JavaSootMethod::hasBody);
  }

  private Method toTarget(Call call) {
    MethodSignature targetSig = call.targetMethodSignature();
    Optional<JavaSootMethod> targetOpt = view.getMethod(targetSig);
    if (targetOpt.isPresent() && targetOpt.get().hasBody()) {
      return JimpleUpMethod.of(targetOpt.get(), view);
    }

    Optional<AbstractInvokeExpr> invokeExprOpt = call.invokableStmt().getInvokeExpr();
    boolean isStaticInvokeExpr = invokeExprOpt.get() instanceof JStaticInvokeExpr;
    return JimpleUpPhantomMethod.of(targetSig, view, isStaticInvokeExpr);
  }

  private Edge toEdge(Statement callSite, Call call) {
    LOGGER.trace("Added edge {} -> {}", callSite, call.targetMethodSignature());
    return new Edge(callSite, toTarget(call));
  }

  @Override
  protected Collection<Edge> computeEdgesOutOf(Statement callSite) {
    if (!(callSite instanceof JimpleUpStatement)
        || !(callSite.getMethod() instanceof JimpleUpMethod)) {
      return List.of();
    }

    JavaSootMethod caller = ((JimpleUpMethod) callSite.getMethod()).getDelegate();
    sootup.core.jimple.common.stmt.Stmt stmt = ((JimpleUpStatement) callSite).getDelegate();

    Collection<Edge> result = new ArrayList<>();
    for (Call call : callGraph.callsFrom(caller.getSignature())) {
      if (call.invokableStmt().equals(stmt) && validSource(call).isPresent()) {
        result.add(toEdge(callSite, call));
      }
    }
    return result;
  }

  @Override
  protected Collection<Edge> computeEdgesInto(Method callee) {
    MethodSignature signature;
    if (callee instanceof JimpleUpMethod) {
      signature = ((JimpleUpMethod) callee).getDelegate().getSignature();
    } else if (callee instanceof JimpleUpPhantomMethod) {
      signature = ((JimpleUpPhantomMethod) callee).getDelegate();
    } else {
      return List.of();
    }

    Collection<Edge> result = new ArrayList<>();
    for (Call call : callGraph.callsTo(signature)) {
      Optional<JavaSootMethod> source = validSource(call);
      if (source.isEmpty()) {
        continue;
      }

      InvokableStmt invokableStmt = call.invokableStmt();
      Statement callSite =
          JimpleUpStatement.create(invokableStmt, JimpleUpMethod.of(source.get(), view));
      Edge edge = toEdge(callSite, call);
      if (edge.tgt().equals(callee)) {
        result.add(edge);
      }
    }
    return result;
  }

  @Override
  protected Collection<Method> computeEntryPoints() {
    Collection<Method> result = new ArrayList<>();
    for (JavaSootMethod m : entryPoints) {
      if (m.hasBody()) {
        result.add(JimpleUpMethod.of(m, view));
        LOGGER.trace("Added entry point: {}", m);
      }
    }
    return result;
  }

  @Override
  protected Collection<Method> computeReachableMethods() {
    Set<Method> result = new LinkedHashSet<>();
    for (Call call : callGraph.getCalls()) {
      if (validSource(call).isPresent()) {
        result.add(toTarget(call));
      }
    }
    return result;
  }
}
