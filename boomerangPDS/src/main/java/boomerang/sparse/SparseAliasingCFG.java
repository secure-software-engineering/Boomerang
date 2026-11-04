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
package boomerang.sparse;

import boomerang.scope.Method;
import boomerang.scope.Statement;
import boomerang.scope.Val;
import com.google.common.graph.Graph;
import com.google.common.graph.ImmutableGraph;
import java.util.Set;
import java.util.function.Predicate;

/**
 * A sparse control flow graph of a method that was built for a query variable. It contains the
 * statements of the original control flow graph that are relevant for the query; an edge s1 -> s2
 * means that s2 is reachable from s1 in the original graph without passing other relevant
 * statements.
 */
public final class SparseAliasingCFG {

  private final Method method;
  private final Val val;
  private final Statement queryStmt;
  private final ImmutableGraph<Statement> graph;
  private final Predicate<Val> tracks;

  /**
   * @param tracks decides for which values the graph is valid, i.e. contains all statements that
   *     are relevant for them
   */
  public SparseAliasingCFG(
      Method method, Val val, Statement queryStmt, Graph<Statement> graph, Predicate<Val> tracks) {
    this.method = method;
    this.val = val;
    this.queryStmt = queryStmt;
    this.graph = ImmutableGraph.copyOf(graph);
    this.tracks = tracks;
  }

  public boolean contains(Statement stmt) {
    return graph.nodes().contains(stmt);
  }

  public Set<Statement> successors(Statement stmt) {
    return graph.successors(stmt);
  }

  public Set<Statement> predecessors(Statement stmt) {
    return graph.predecessors(stmt);
  }

  public Graph<Statement> getGraph() {
    return graph;
  }

  public Method getMethod() {
    return method;
  }

  /**
   * @return the value this graph was built for
   */
  public Val getVal() {
    return val;
  }

  public Statement getQueryStmt() {
    return queryStmt;
  }

  /**
   * @param val a value without unbalanced marker
   * @return true if the graph contains all statements that are relevant for the value
   */
  public boolean tracks(Val val) {
    return tracks.test(val);
  }

  @Override
  public String toString() {
    return "SparseAliasingCFG of "
        + method
        + " for "
        + val
        + " @ "
        + queryStmt
        + " ("
        + graph.nodes().size()
        + " statements)";
  }
}
