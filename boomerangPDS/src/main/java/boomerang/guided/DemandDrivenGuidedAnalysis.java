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
package boomerang.guided;

import boomerang.BackwardQuery;
import boomerang.Boomerang;
import boomerang.ForwardQuery;
import boomerang.Query;
import boomerang.QueryGraph;
import boomerang.guided.Specification.QueryDirection;
import boomerang.options.BoomerangOptions;
import boomerang.results.AbstractBoomerangResults.Context;
import boomerang.results.BackwardBoomerangResults;
import boomerang.results.ForwardBoomerangResults;
import boomerang.scope.ControlFlowGraph.Edge;
import boomerang.scope.FrameworkScope;
import boomerang.scope.Val;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;
import com.google.common.collect.Table.Cell;
import java.util.*;
import java.util.Map.Entry;
import sync.pds.solver.nodes.Node;
import wpds.impl.Weight.NoWeight;

public class DemandDrivenGuidedAnalysis {

  private final IDemandDrivenGuidedManager spec;
  private final LinkedList<QueryWithContext> queryQueue = Lists.newLinkedList();
  private final Set<Query> visited = Sets.newHashSet();
  private final Boomerang solver;
  private boolean triggered;

  public DemandDrivenGuidedAnalysis(
      IDemandDrivenGuidedManager specification,
      BoomerangOptions options,
      FrameworkScope frameworkScope) {
    this.spec = specification;
    if (!options.allowMultipleQueries()) {
      throw new RuntimeException(
          "Boomerang options allowMultipleQueries is set to false. Please enable it.");
    }
    this.solver = new Boomerang(frameworkScope, options);
  }

  /**
   * The query graph takes as input an initial query from which all follow up computations are
   * computed. Based on the specification provided. It returns the QueryGraph which is a graph whose
   * nodes are Boomerang Queries, there is an edge between the queries if there node A triggered a
   * subsequent query B.
   *
   * <p>Important note: Ensure to call cleanUp() after finishing the analysis.
   *
   * @param query The initial query to start the analysis from.
   * @return a query graph containing all queries triggered.
   */
  public QueryGraph<NoWeight> run(Query query) {
    if (triggered) {
      throw new RuntimeException(
          DemandDrivenGuidedAnalysis.class.getName()
              + " must be instantiated once per query. Use a different instance.");
    }
    triggered = true;
    queryQueue.add(new QueryWithContext(query));

    while (!queryQueue.isEmpty()) {
      QueryWithContext pop = queryQueue.pop();
      if (pop.query instanceof ForwardQuery) {
        ForwardBoomerangResults<NoWeight> results;
        ForwardQuery currentQuery = (ForwardQuery) pop.query;
        if (pop.parentQuery == null) {
          results = solver.solve(currentQuery);
        } else {
          results = solver.solveUnderScope(currentQuery, pop.triggeringNode, pop.parentQuery);
        }

        Table<Edge, Val, NoWeight> forwardResults =
            results.asEdgeValWeightTable((ForwardQuery) pop.query);
        // Any ForwardQuery may trigger additional ForwardQuery under its own scope.
        triggerNewQueries(forwardResults, currentQuery, QueryDirection.FORWARD);
      } else {
        BackwardBoomerangResults<NoWeight> results;
        if (pop.parentQuery == null) {
          results = solver.solve((BackwardQuery) pop.query);
        } else {
          results =
              solver.solveUnderScope(
                  (BackwardQuery) pop.query, pop.triggeringNode, pop.parentQuery);
        }

        Table<Edge, Val, NoWeight> backwardResults =
            solver.getBackwardSolvers().get(query).asEdgeValWeightTable();
        // TODO: [ms] figure out why its structurally
        // different than with Forwardqueries - potential?
        triggerNewQueries(backwardResults, pop.query, QueryDirection.BACKWARD);
        Map<ForwardQuery, Context> allocationSites = results.getAllocationSites();

        for (Entry<ForwardQuery, Context> entry : allocationSites.entrySet()) {
          triggerNewQueries(
              results.asEdgeValWeightTable(entry.getKey()), entry.getKey(), QueryDirection.FORWARD);
        }
      }
    }

    QueryGraph<NoWeight> queryGraph = solver.getQueryGraph();
    return queryGraph;
  }

  /**
   * Ensure to call cleanup to detach all listeners from the solver, otherwise the analysis may run
   * into a Memory issues.
   */
  public void cleanUp() {
    solver.unregisterAllListeners();
  }

  public Boomerang getSolver() {
    return solver;
  }

  private void triggerNewQueries(
      Table<Edge, Val, NoWeight> backwardResults, Query lastQuery, QueryDirection direction) {
    for (Cell<Edge, Val, NoWeight> cell : backwardResults.cellSet()) {
      Edge triggeringEdge = cell.getRowKey();
      Val fact = cell.getColumnKey();
      Collection<Query> queries;
      if (direction == QueryDirection.FORWARD) {
        queries =
            spec.onForwardFlow((ForwardQuery) lastQuery, cell.getRowKey(), cell.getColumnKey());
      } else {
        queries =
            spec.onBackwardFlow((BackwardQuery) lastQuery, cell.getRowKey(), cell.getColumnKey());
      }
      for (Query q : queries) {
        if (visited.add(q)) {
          QueryWithContext nextQuery =
              new QueryWithContext(q, new Node<>(triggeringEdge, fact), lastQuery);
          queryQueue.add(nextQuery);
        }
      }
    }
  }

  private static class QueryWithContext {
    private QueryWithContext(Query query) {
      this.query = query;
    }

    private QueryWithContext(Query query, Node<Edge, Val> triggeringNode, Query parentQuery) {
      this.query = query;
      this.parentQuery = parentQuery;
      this.triggeringNode = triggeringNode;
    }

    Query query;
    Query parentQuery;
    Node<Edge, Val> triggeringNode;
  }
}
