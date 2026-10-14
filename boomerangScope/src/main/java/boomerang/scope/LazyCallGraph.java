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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * A {@link CallGraph} that answers queries on demand from a framework's own call graph instead of
 * copying it up front. Results are wrapped once and memoized, so only the part of the graph an
 * analysis actually visits is ever held in scope objects. The framework call graph is kept alive by
 * this object.
 *
 * <p>The graph is read-only: {@link #addEdge(Edge)} and {@link #addEntryPoint(Method)} throw.
 */
public abstract class LazyCallGraph extends CallGraph {

  private final Map<Statement, Collection<Edge>> edgesOutOfCache = new HashMap<>();
  private final Map<Method, Collection<Edge>> edgesIntoCache = new HashMap<>();
  private Collection<Method> entryPoints;
  private Set<Method> reachableMethods;
  private Set<Edge> edges;

  /** Call graph edges whose call site is {@code callSite}; only asked for invoke statements. */
  protected abstract Collection<Edge> computeEdgesOutOf(Statement callSite);

  /** Call graph edges whose target is {@code callee}. */
  protected abstract Collection<Edge> computeEdgesInto(Method callee);

  /** Entry points of the graph; called at most once. */
  protected abstract Collection<Method> computeEntryPoints();

  /** Entry points and every edge target; called at most once. */
  protected abstract Collection<Method> computeReachableMethods();

  @Override
  public Collection<Edge> edgesOutOf(Statement stmt) {
    Collection<Edge> cached = edgesOutOfCache.get(stmt);
    if (cached != null) {
      return cached;
    }

    Collection<Edge> result =
        stmt.containsInvokeExpr()
            ? Collections.unmodifiableSet(new LinkedHashSet<>(computeEdgesOutOf(stmt)))
            : Collections.emptySet();
    edgesOutOfCache.put(stmt, result);
    return result;
  }

  @Override
  public Collection<Edge> edgesInto(Method m) {
    Collection<Edge> cached = edgesIntoCache.get(m);
    if (cached != null) {
      return cached;
    }

    Collection<Edge> result = Collections.unmodifiableSet(new LinkedHashSet<>(computeEdgesInto(m)));
    edgesIntoCache.put(m, result);
    return result;
  }

  @Override
  public Collection<Method> getEntryPoints() {
    if (entryPoints == null) {
      entryPoints = Collections.unmodifiableSet(new LinkedHashSet<>(computeEntryPoints()));
    }
    return entryPoints;
  }

  @Override
  public Set<Method> getReachableMethods() {
    if (reachableMethods == null) {
      Set<Method> methods = new LinkedHashSet<>(getEntryPoints());
      methods.addAll(computeReachableMethods());
      reachableMethods = Collections.unmodifiableSet(methods);
    }
    return reachableMethods;
  }

  /**
   * All edges of the graph; called at most once. The default visits every call site of the
   * reachable methods. Frameworks whose graph has edges out of methods that are no entry point and
   * no target (e.g. implicit static initializer calls) override this to include them.
   */
  protected Collection<Edge> computeAllEdges() {
    Set<Edge> result = new LinkedHashSet<>();
    for (Method m : getReachableMethods()) {
      if (!m.isDefined()) {
        continue;
      }
      for (Statement s : m.getStatements()) {
        if (s.containsInvokeExpr()) {
          result.addAll(edgesOutOf(s));
        }
      }
    }
    return result;
  }

  /**
   * Materializes every edge. This is as expensive as an eager copy and only meant for debugging and
   * statistics.
   */
  @Override
  public Set<Edge> getEdges() {
    if (edges == null) {
      edges = Collections.unmodifiableSet(new LinkedHashSet<>(computeAllEdges()));
    }
    return edges;
  }

  @Override
  public int size() {
    return getEdges().size();
  }

  @Override
  public boolean addEdge(Edge edge) {
    throw new UnsupportedOperationException("A lazy call graph is read-only");
  }

  @Override
  public boolean addEntryPoint(Method m) {
    throw new UnsupportedOperationException("A lazy call graph is read-only");
  }
}
