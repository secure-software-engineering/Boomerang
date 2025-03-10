/**
 * ***************************************************************************** 
 * Copyright (c) 2025 Fraunhofer IEM, Paderborn, Germany. This program and the
 * accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at http://www.eclipse.org/legal/epl-2.0.
 *
 * <p>SPDX-License-Identifier: EPL-2.0
 *
 * <p>Contributors: Johannes Spaeth - initial API and implementation
 * *****************************************************************************
 */
package sparse;

import boomerang.scope.Val;
import com.google.common.graph.MutableGraph;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SparseAliasingCFG<U, V> {

  private static final Logger log = LoggerFactory.getLogger(SparseAliasingCFG.class);

  private final MutableGraph<U> graph;
  private final Val d; // which dff this SCFG belongs to
  private final U queryStmt; // in contrast to sparseCFG queryStmt affects the graph
  private final Set<V> fallbackAliases;
  private final Map<U, Integer> unitToNumber;

  public SparseAliasingCFG(
      Val d,
      MutableGraph<U> graph,
      U queryStmt,
      Set<V> fallbackAliases,
      Map<U, Integer> unitToNumber) {
    this.d = d;
    this.queryStmt = queryStmt;
    this.graph = graph;
    this.fallbackAliases = fallbackAliases;
    this.unitToNumber = unitToNumber;
  }

  public Set<V> getFallBackAliases() {
    return fallbackAliases;
  }

  public synchronized boolean addEdge(U node, U succ) {
    return graph.putEdge(node, succ);
  }

  public Set<U> getSuccessors(U node) {
    return graph.successors(node);
  }

  public List<U> getNextUses(U node) {
    Set<U> successors = getSuccessors(node);
    return new ArrayList<>(successors);
  }

  public MutableGraph<U> getGraph() {
    return this.graph;
  }
}
