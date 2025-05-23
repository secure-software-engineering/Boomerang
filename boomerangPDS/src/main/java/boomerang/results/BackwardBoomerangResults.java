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
package boomerang.results;

import boomerang.BackwardQuery;
import boomerang.ForwardQuery;
import boomerang.Query;
import boomerang.Util;
import boomerang.scope.ControlFlowGraph.Edge;
import boomerang.scope.Statement;
import boomerang.scope.Type;
import boomerang.scope.Val;
import boomerang.solver.BackwardBoomerangSolver;
import boomerang.solver.ForwardBoomerangSolver;
import boomerang.stats.IBoomerangStats;
import boomerang.util.AccessPath;
import boomerang.util.DefaultValueMap;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Maps;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import sync.pds.solver.nodes.INode;
import sync.pds.solver.nodes.Node;
import wpds.impl.Transition;
import wpds.impl.Weight;
import wpds.impl.WeightedPAutomaton;

public class BackwardBoomerangResults<W extends Weight> extends AbstractBoomerangResults<W> {

  private final BackwardQuery query;
  private final BackwardBoomerangSolver<W> backwardSolver;
  private Map<ForwardQuery, Context> allocationSites;
  private final boolean timedout;
  private final IBoomerangStats<W> stats;
  private final Stopwatch analysisWatch;
  private final long maxMemory;

  public BackwardBoomerangResults(
      BackwardQuery query,
      boolean timedout,
      DefaultValueMap<ForwardQuery, ForwardBoomerangSolver<W>> queryToSolvers,
      BackwardBoomerangSolver<W> backwardSolver,
      IBoomerangStats<W> stats,
      Stopwatch analysisWatch) {
    super(queryToSolvers);
    this.query = query;
    this.timedout = timedout;
    this.stats = stats;
    this.analysisWatch = analysisWatch;
    this.backwardSolver = backwardSolver;
    stats.terminated(query, this);
    maxMemory = Util.getReallyUsedMemory();
  }

  public Map<ForwardQuery, Context> getAllocationSites() {
    computeAllocations();
    return allocationSites;
  }

  public boolean isTimedout() {
    return timedout;
  }

  public IBoomerangStats<W> getStats() {
    return stats;
  }

  public Stopwatch getAnalysisWatch() {
    return analysisWatch;
  }

  private void computeAllocations() {
    if (allocationSites != null) return;
    final Set<ForwardQuery> results = new LinkedHashSet<>();
    for (final Entry<ForwardQuery, ForwardBoomerangSolver<W>> fw : queryToSolvers.entrySet()) {
      for (INode<Node<Edge, Val>> node : fw.getValue().getFieldAutomaton().getInitialStates())
        fw.getValue()
            .getFieldAutomaton()
            .registerListener(
                new ExtractAllocationSiteStateListener<W>(node, query, fw.getKey()) {

                  @Override
                  protected void allocationSiteFound(
                      ForwardQuery allocationSite, BackwardQuery query) {
                    results.add(allocationSite);
                  }
                });
    }
    allocationSites = Maps.newHashMap();
    for (ForwardQuery q : results) {
      Context context = constructContextGraph(q, query.asNode());
      assert allocationSites.get(q) == null;
      allocationSites.put(q, context);
    }
  }

  public boolean aliases(Query el) {
    for (final ForwardQuery fw : getAllocationSites().keySet()) {
      if (queryToSolvers.getOrCreate(fw).getReachedStates().contains(el.asNode())) {
        if (queryToSolvers.getOrCreate(fw).reachesNodeWithEmptyField(el.asNode())) {
          return true;
        }
      }
    }
    return false;
  }

  public Set<AccessPath> getAllAliases(Edge stmt) {
    final Set<AccessPath> results = new LinkedHashSet<>();
    for (final ForwardQuery fw : getAllocationSites().keySet()) {
      queryToSolvers
          .getOrCreate(fw)
          .registerListener(
              new ExtractAllAliasListener<W>(this.queryToSolvers.get(fw), results, stmt));
    }
    return results;
  }

  /**
   * Return an {@link AccessPath} for each aliasing object of the original query.
   *
   * @return all access paths aliasing with the query variable
   */
  public Set<AccessPath> getAllAliases() {
    return getAllAliases(query.cfgEdge());
  }

  public boolean isEmpty() {
    computeAllocations();
    return allocationSites.isEmpty();
  }

  /**
   * Returns the set of types the backward analysis for the triggered query ever propagates.
   *
   * @return Set of types the backward analysis propagates
   */
  public Set<Type> getPropagationType() {
    Set<Type> types = new LinkedHashSet<>();
    for (Transition<Edge, INode<Val>> t : backwardSolver.getCallAutomaton().getTransitions()) {
      if (!t.getStart().fact().isStatic()) types.add(t.getStart().fact().getType());
    }
    return types;
  }

  /**
   * Computes the set of statements (and variables at these statements) relevant for data-flow
   * propagation. A statement s is relevant, if a propagated variable x is used at s. I.e., when
   * propagting x @ y = x, the returned set contains x @ y = x, whereas it will not contain a call
   * site x @ y = foo(c), because x is not used at the statement.
   *
   * @return The set of relevant statements during data-flow propagation
   */
  @Deprecated
  public Set<Node<Edge, Val>> getDataFlowPath(ForwardQuery query) {
    Set<Node<Edge, Val>> dataFlowPath = new LinkedHashSet<>();
    WeightedPAutomaton<Edge, INode<Val>, W> callAut =
        queryToSolvers.getOrCreate(query).getCallAutomaton();
    for (Entry<Transition<Edge, INode<Val>>, W> e :
        callAut.getTransitionsToFinalWeights().entrySet()) {
      Transition<Edge, INode<Val>> t = e.getKey();
      if (t.getLabel().equals(Statement.epsilon())) continue;
      if (t.getStart().fact().isLocal()
          && !t.getLabel().getMethod().equals(t.getStart().fact().m())) continue;
      if (t.getLabel().getStart().uses(t.getStart().fact()))
        dataFlowPath.add(new Node<>(t.getLabel(), t.getStart().fact()));
    }
    return dataFlowPath;
  }

  public long getMaxMemory() {
    return maxMemory;
  }
}
