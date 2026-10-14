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
package boomerang.scope.soot;

import boomerang.scope.LazyCallGraph;
import boomerang.scope.Method;
import boomerang.scope.Statement;
import boomerang.scope.soot.jimple.JimpleMethod;
import boomerang.scope.soot.jimple.JimplePhantomMethod;
import boomerang.scope.soot.jimple.JimpleStatement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import soot.Scene;
import soot.SootMethod;
import soot.Unit;

/**
 * Forwards to Soot's call graph. The Soot graph stays referenced by the {@link Scene} anyway (until
 * {@link Scene#releaseCallGraph()}), so forwarding costs no extra memory over copying it.
 */
public class SootCallGraph extends LazyCallGraph {

  private final Scene scene;
  private final soot.jimple.toolkits.callgraph.CallGraph callGraph;
  private final List<SootMethod> entryPoints;

  public SootCallGraph(
      Scene scene,
      soot.jimple.toolkits.callgraph.CallGraph callGraph,
      Collection<SootMethod> entryPoints) {
    this.scene = scene;
    this.callGraph = callGraph;
    this.entryPoints = new ArrayList<>(entryPoints);

    if (!hasEdges()) {
      throw new IllegalStateException("CallGraph is empty!");
    }
  }

  private boolean hasEdges() {
    for (soot.jimple.toolkits.callgraph.Edge e : callGraph) {
      if (isValid(e)) {
        return true;
      }
    }
    return false;
  }

  private static boolean isValid(soot.jimple.toolkits.callgraph.Edge e) {
    return e.src().hasActiveBody() && e.srcStmt() != null && e.srcStmt().containsInvokeExpr();
  }

  private Method toTarget(SootMethod tgt) {
    // Distinguish between loaded methods and phantom methods to cover all existing edges
    if (tgt.hasActiveBody()) {
      return JimpleMethod.of(tgt, scene);
    }
    return JimplePhantomMethod.of(tgt.makeRef(), scene);
  }

  private Edge toEdge(Statement callSite, soot.jimple.toolkits.callgraph.Edge e) {
    Method target = toTarget(e.tgt());
    LOGGER.trace("Call edge from {} to target method {}", callSite, e.tgt());
    return new Edge(callSite, target);
  }

  @Override
  protected Collection<Edge> computeEdgesOutOf(Statement callSite) {
    if (!(callSite instanceof JimpleStatement)) {
      return List.of();
    }

    Unit unit = ((JimpleStatement) callSite).getDelegate();
    Collection<Edge> result = new ArrayList<>();
    for (Iterator<soot.jimple.toolkits.callgraph.Edge> it = callGraph.edgesOutOf(unit);
        it.hasNext(); ) {
      soot.jimple.toolkits.callgraph.Edge e = it.next();
      if (isValid(e)) {
        result.add(toEdge(callSite, e));
      }
    }
    return result;
  }

  @Override
  protected Collection<Edge> computeEdgesInto(Method callee) {
    SootMethod sootMethod;
    if (callee instanceof JimpleMethod) {
      sootMethod = ((JimpleMethod) callee).getDelegate();
    } else if (callee instanceof JimplePhantomMethod) {
      sootMethod = ((JimplePhantomMethod) callee).getDelegate().tryResolve();
    } else {
      sootMethod = null;
    }
    if (sootMethod == null) {
      return List.of();
    }

    Collection<Edge> result = new ArrayList<>();
    for (Iterator<soot.jimple.toolkits.callgraph.Edge> it = callGraph.edgesInto(sootMethod);
        it.hasNext(); ) {
      soot.jimple.toolkits.callgraph.Edge e = it.next();
      if (!isValid(e)) {
        continue;
      }

      Statement callSite = JimpleStatement.create(e.srcStmt(), JimpleMethod.of(e.src(), scene));
      Edge edge = toEdge(callSite, e);
      // A phantom callee must be the same method the edge resolves to
      if (edge.tgt().equals(callee)) {
        result.add(edge);
      }
    }
    return result;
  }

  @Override
  protected Collection<Edge> computeAllEdges() {
    Collection<Edge> result = new ArrayList<>();
    for (soot.jimple.toolkits.callgraph.Edge e : callGraph) {
      if (isValid(e)) {
        Statement callSite = JimpleStatement.create(e.srcStmt(), JimpleMethod.of(e.src(), scene));
        result.add(toEdge(callSite, e));
      }
    }
    return result;
  }

  @Override
  protected Collection<Method> computeEntryPoints() {
    Collection<Method> result = new ArrayList<>();
    for (SootMethod m : entryPoints) {
      if (m.hasActiveBody()) {
        result.add(JimpleMethod.of(m, scene));
      }
    }
    return result;
  }

  @Override
  protected Collection<Method> computeReachableMethods() {
    Set<Method> result = new LinkedHashSet<>();
    for (soot.jimple.toolkits.callgraph.Edge e : callGraph) {
      if (isValid(e)) {
        result.add(toTarget(e.tgt()));
      }
    }
    return result;
  }
}
