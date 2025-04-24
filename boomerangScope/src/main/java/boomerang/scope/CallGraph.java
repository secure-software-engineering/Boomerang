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
package boomerang.scope;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CallGraph {

  protected static final Logger LOGGER = LoggerFactory.getLogger(CallGraph.class);
  private final Set<Edge> edges = new LinkedHashSet<>();
  private final Multimap<Statement, Edge> edgesOutOf = HashMultimap.create();
  private final Multimap<Method, Edge> edgesInto = HashMultimap.create();
  private final Set<Method> entryPoints = new LinkedHashSet<>();
  private final Multimap<Field, Statement> fieldLoadStatements = HashMultimap.create();
  private final Multimap<Field, Statement> fieldStoreStatements = HashMultimap.create();

  public Collection<Edge> edgesOutOf(Statement stmt) {
    return edgesOutOf.get(stmt);
  }

  public static class Edge {

    private final Statement callSite;
    private final Method callee;

    public Edge(Statement callSite, Method callee) {
      assert callSite.containsInvokeExpr();
      this.callSite = callSite;
      this.callee = callee;
    }

    public Method tgt() {
      return callee;
    }

    public Statement src() {
      return callSite;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      Edge edge = (Edge) o;
      return Objects.equals(callSite, edge.callSite) && Objects.equals(callee, edge.callee);
    }

    @Override
    public int hashCode() {
      return Objects.hash(callSite, callee);
    }

    @Override
    public String toString() {
      return "Call Graph Edge: " + callSite + " calls " + tgt();
    }
  }

  public boolean addEdge(Edge edge) {
    edgesOutOf.put(edge.callSite, edge);
    edgesInto.put(edge.tgt(), edge);

    if (edge.tgt().isDefined()) {
      computeStaticFieldsLoadAndStores(edge.tgt());
    }

    return edges.add(edge);
  }

  public Collection<Edge> edgesInto(Method m) {
    return edgesInto.get(m);
  }

  public int size() {
    return edges.size();
  }

  public Set<Edge> getEdges() {
    return edges;
  }

  public Collection<Method> getEntryPoints() {
    return entryPoints;
  }

  public boolean addEntryPoint(Method m) {
    computeStaticFieldsLoadAndStores(m);
    return entryPoints.add(m);
  }

  public Set<Method> getReachableMethods() {
    Set<Method> reachableMethod = new LinkedHashSet<>();
    reachableMethod.addAll(entryPoints);
    reachableMethod.addAll(edgesInto.keySet());
    return reachableMethod;
  }

  public Multimap<Field, Statement> getFieldStoreStatements() {
    return fieldStoreStatements;
  }

  public Multimap<Field, Statement> getFieldLoadStatements() {
    return fieldLoadStatements;
  }

  private void computeStaticFieldsLoadAndStores(Method m) {
    for (Statement s : m.getStatements()) {
      if (s.isStaticFieldStore()) {
        fieldStoreStatements.put(s.getStaticField().field(), s);
      }
      if (s.isStaticFieldLoad()) {
        fieldLoadStatements.put(s.getStaticField().field(), s);
      }
    }
  }
}
