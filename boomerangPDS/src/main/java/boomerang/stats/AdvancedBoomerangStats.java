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
package boomerang.stats;

import boomerang.BackwardQuery;
import boomerang.ForwardQuery;
import boomerang.Query;
import boomerang.WeightedBoomerang;
import boomerang.results.BackwardBoomerangResults;
import boomerang.results.ForwardBoomerangResults;
import boomerang.scope.ControlFlowGraph.Edge;
import boomerang.scope.Field;
import boomerang.scope.Field.ArrayField;
import boomerang.scope.Method;
import boomerang.scope.Val;
import boomerang.solver.AbstractBoomerangSolver;
import boomerang.solver.BackwardBoomerangSolver;
import boomerang.solver.ForwardBoomerangSolver;
import com.google.common.collect.Maps;
import de.fraunhofer.iem.Location;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import sync.pds.solver.nodes.INode;
import sync.pds.solver.nodes.Node;
import wpds.impl.Rule;
import wpds.impl.Transition;
import wpds.impl.Weight;
import wpds.interfaces.State;

public class AdvancedBoomerangStats<W extends Weight> implements IBoomerangStats<W> {

  private final Map<Query, AbstractBoomerangSolver<W>> queries = Maps.newHashMap();
  private final Set<WeightedTransition<Field, INode<Node<Edge, Val>>, W>> globalFieldTransitions =
      new LinkedHashSet<>();
  private int fieldTransitionCollisions;
  private final Set<WeightedTransition<Edge, INode<Val>, W>> globalCallTransitions =
      new LinkedHashSet<>();
  private int callTransitionCollisions;
  private final Set<Rule<Field, INode<Node<Edge, Val>>, W>> globalFieldRules =
      new LinkedHashSet<>();
  private int fieldRulesCollisions;
  private final Set<Rule<Edge, INode<Val>, W>> globalCallRules = new LinkedHashSet<>();
  private int callRulesCollisions;
  private final Set<Node<Edge, Val>> reachedForwardNodes = new LinkedHashSet<>();
  private int reachedForwardNodeCollisions;

  private final Set<Node<Edge, Val>> reachedBackwardNodes = new LinkedHashSet<>();
  private int reachedBackwardNodeCollisions;
  private final Set<Method> callVisitedMethods = new LinkedHashSet<>();
  private final Set<Method> fieldVisitedMethods = new LinkedHashSet<>();
  private int arrayFlows;
  private int staticFlows;
  private final boolean COUNT_TOP_METHODS = false;
  private final Map<String, Integer> backwardFieldMethodsRules = new TreeMap<>();
  private final Map<String, Integer> backwardCallMethodsRules = new TreeMap<>();

  private final Map<String, Integer> forwardFieldMethodsRules = new TreeMap<>();
  private final Map<String, Integer> forwardCallMethodsRules = new TreeMap<>();

  public static <K> Map<K, Integer> sortByValues(final Map<K, Integer> map) {
    Comparator<K> valueComparator =
        (k1, k2) -> {
          if (map.get(k2) > map.get(k1)) return 1;
          else return -1;
        };
    Map<K, Integer> sortedByValues = new TreeMap<K, Integer>(valueComparator);
    sortedByValues.putAll(map);
    return sortedByValues;
  }

  @Override
  public void registerSolver(Query key, final AbstractBoomerangSolver<W> solver) {
    if (queries.containsKey(key)) {
      return;
    }
    queries.put(key, solver);
    solver
        .getFieldAutomaton()
        .registerListener(
            (t, w, aut) -> {
              if (!globalFieldTransitions.add(new WeightedTransition<>(t, w))) {
                fieldTransitionCollisions++;
              }
              fieldVisitedMethods.add(t.getStart().fact().stmt().getMethod());
              if (t.getLabel() instanceof ArrayField) {
                arrayFlows++;
              }
            });

    solver
        .getCallAutomaton()
        .registerListener(
            (t, w, aut) -> {
              if (!globalCallTransitions.add(new WeightedTransition<>(t, w))) {
                callTransitionCollisions++;
              }
              callVisitedMethods.add(t.getLabel().getMethod());

              if (t.getStart().fact().isStatic()) {
                staticFlows++;
              }
            });

    solver
        .getFieldPDS()
        .registerUpdateListener(
            rule -> {
              if (!globalFieldRules.add(rule)) {
                fieldRulesCollisions++;
              } else if (COUNT_TOP_METHODS) {
                increaseMethod(
                    rule.getS1().fact().stmt().getMethod().toString(),
                    (solver instanceof BackwardBoomerangSolver
                        ? backwardFieldMethodsRules
                        : forwardFieldMethodsRules));
              }
            });
    solver
        .getCallPDS()
        .registerUpdateListener(
            rule -> {
              if (!globalCallRules.add(rule)) {
                callRulesCollisions++;

              } else if (COUNT_TOP_METHODS) {
                increaseMethod(
                    rule.getL1().getMethod().toString(),
                    (solver instanceof BackwardBoomerangSolver
                        ? backwardCallMethodsRules
                        : forwardCallMethodsRules));
              }
            });

    solver.registerListener(
        reachableNode -> {
          if (solver instanceof ForwardBoomerangSolver) {
            if (!reachedForwardNodes.add(reachableNode)) {
              reachedForwardNodeCollisions++;
            }
          } else {
            if (!reachedBackwardNodes.add(reachableNode)) {
              reachedBackwardNodeCollisions++;
            }
          }
        });
  }

  private void increaseMethod(String method, Map<String, Integer> map) {
    Integer i = map.get(method);
    if (i == null) {
      i = Integer.valueOf(0);
    }
    map.put(method, ++i);
  }

  @Override
  public void registerFieldWritePOI(WeightedBoomerang<W>.FieldWritePOI key) {}

  public String toString() {
    String s = "=========== Boomerang Stats =============\n";
    int forwardQuery = 0;
    int backwardQuery = 0;
    for (Query q : queries.keySet()) {
      if (q instanceof ForwardQuery) {
        forwardQuery++;
      } else backwardQuery++;
    }
    s +=
        String.format(
            "Queries (Forward/Backward/Total): \t\t %s/%s/%s\n",
            forwardQuery, backwardQuery, queries.size());
    s +=
        String.format(
            "Visited Methods (Field/Call): \t\t %s/%s\n",
            fieldVisitedMethods.size(), callVisitedMethods.size());
    s +=
        String.format(
            "Reached Forward Nodes(Collisions): \t\t %s (%s)\n",
            reachedForwardNodes.size(), reachedForwardNodeCollisions);
    s +=
        String.format(
            "Reached Backward Nodes(Collisions): \t\t %s (%s)\n",
            reachedBackwardNodes.size(), reachedBackwardNodeCollisions);
    s +=
        String.format(
            "Global Field Rules(Collisions): \t\t %s (%s)\n",
            globalFieldRules.size(), fieldRulesCollisions);
    s +=
        String.format(
            "Global Field Transitions(Collisions): \t\t %s (%s)\n",
            globalFieldTransitions.size(), fieldTransitionCollisions);
    s +=
        String.format(
            "Global Call Rules(Collisions): \t\t %s (%s)\n",
            globalCallRules.size(), callRulesCollisions);
    s +=
        String.format(
            "Global Call Transitions(Collisions): \t\t %s (%s)\n",
            globalCallTransitions.size(), callTransitionCollisions);
    s +=
        String.format(
            "Special Flows (Static/Array): \t\t %s(%s)/%s(%s)\n",
            staticFlows, globalCallTransitions.size(), arrayFlows, globalFieldTransitions.size());
    if (COUNT_TOP_METHODS) {
      s += topMostMethods(forwardFieldMethodsRules, "forward field");
      s += topMostMethods(forwardCallMethodsRules, "forward call");

      if (!backwardCallMethodsRules.isEmpty()) {
        s += topMostMethods(backwardFieldMethodsRules, "backward field");
        s += topMostMethods(backwardCallMethodsRules, "backward call");
      }
    }
    s += computeMetrics();
    s += "\n";
    return s;
  }

  private String topMostMethods(Map<String, Integer> fieldMethodsRules, String system) {
    Map<String, Integer> sootMethodIntegerMap = sortByValues(fieldMethodsRules);
    int i = 0;
    String s = "";
    for (Map.Entry<String, Integer> e : sootMethodIntegerMap.entrySet()) {
      if (++i > 11) break;
      s +=
          String.format(
              "%s. most %s visited Method(%sx): %s\n", i, system, e.getValue(), e.getKey());
    }
    return s;
  }

  @Override
  public Set<Method> getCallVisitedMethods() {
    return new LinkedHashSet(callVisitedMethods);
  }

  private String computeMetrics() {
    int min = Integer.MAX_VALUE;
    int totalReached = 0;
    int max = 0;
    Query maxQuery = null;
    for (Query q : queries.keySet()) {
      int size = queries.get(q).getReachedStates().size();
      totalReached += size;
      min = Math.min(size, min);
      if (size > max) {
        maxQuery = q;
      }
      max = Math.max(size, max);
    }
    float average = ((float) totalReached) / queries.size();
    String s = String.format("Reachable nodes (Min/Avg/Max): \t\t%s/%s/%s\n", min, average, max);
    s += String.format("Maximal Query: \t\t%s\n", maxQuery);
    return s;
  }

  private static class WeightedTransition<X extends Location, Y extends State, W> {
    final Transition<X, Y> t;
    final W w;

    public WeightedTransition(Transition<X, Y> t, W w) {
      this.t = t;
      this.w = w;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((t == null) ? 0 : t.hashCode());
      result = prime * result + ((w == null) ? 0 : w.hashCode());
      return result;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null) return false;
      if (getClass() != obj.getClass()) return false;
      WeightedTransition other = (WeightedTransition) obj;
      if (t == null) {
        if (other.t != null) return false;
      } else if (!t.equals(other.t)) return false;
      if (w == null) {
        return other.w == null;
      } else return w.equals(other.w);
    }
  }

  @Override
  public Collection<? extends Node<Edge, Val>> getForwardReachesNodes() {
    Set<Node<Edge, Val>> res = new LinkedHashSet<>();
    for (Query q : queries.keySet()) {
      if (q instanceof ForwardQuery) res.addAll(queries.get(q).getReachedStates());
    }
    return res;
  }

  @Override
  public void terminated(ForwardQuery query, ForwardBoomerangResults<W> forwardBoomerangResults) {
    // TODO Auto-generated method stub

  }

  @Override
  public void terminated(
      BackwardQuery query, BackwardBoomerangResults<W> backwardBoomerangResults) {
    // TODO Auto-generated method stub

  }
}
