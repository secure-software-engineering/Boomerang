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
package boomerang.stats;

import boomerang.BackwardQuery;
import boomerang.ForwardQuery;
import boomerang.Query;
import boomerang.Util;
import boomerang.WeightedBoomerang;
import boomerang.results.BackwardBoomerangResults;
import boomerang.results.ForwardBoomerangResults;
import boomerang.scope.ControlFlowGraph.Edge;
import boomerang.scope.Field;
import boomerang.scope.Field.ArrayField;
import boomerang.scope.Method;
import boomerang.scope.Val;
import boomerang.solver.AbstractBoomerangSolver;
import boomerang.solver.ForwardBoomerangSolver;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import de.fraunhofer.iem.Location;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import sync.pds.solver.nodes.GeneratedState;
import sync.pds.solver.nodes.INode;
import sync.pds.solver.nodes.Node;
import wpds.impl.Rule;
import wpds.impl.Transition;
import wpds.impl.Weight;
import wpds.interfaces.State;

public class CSVBoomerangStatsWriter<W extends Weight> implements IBoomerangStats<W> {

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
  private final Set<Edge> callVisitedStmts = new LinkedHashSet<>();
  private final Set<Edge> fieldVisitedStmts = new LinkedHashSet<>();
  private final Set<INode<Node<Edge, Val>>> fieldGeneratedStates = new LinkedHashSet<>();
  private final Set<INode<Val>> callGeneratedStates = new LinkedHashSet<>();
  private int arrayFlows;
  private int staticFlows;
  private int fieldWritePOIs;
  private int fieldReadPOIs;

  private final String outputFileName;
  private static final String CSV_SEPARATOR = ";";
  private final List<String> headers = Lists.newArrayList();
  private final Map<String, String> headersToValues = Maps.newHashMap();
  private final long memoryBefore;

  private enum Headers {
    Query,
    QueryType,
    FieldTransitions,
    CallTransitions,
    CallRules,
    FieldRules,
    ReachedForwardNodes,
    ReachedBackwardNodes,
    CallVisitedMethods,
    FieldVisitedMethods,
    CallVisitedStmts,
    FieldVisitedStmts,
    FieldWritePOIs,
    FieldReadPOIs,
    StaticFlows,
    ArrayFlows,
    QueryTime,
    Timeout,
    ICFGEdges,
    CallGeneratedStates,
    FieldGeneratedStates,
    FieldLongestAccessPath,
    CallLongestCallStack,
    CallContainsLoop,
    FieldContainsLoop,
    MemoryBefore,
    MemoryAfter,
    MemoryDiff
  }

  public CSVBoomerangStatsWriter(String outputFileName) {
    this.outputFileName = outputFileName;
    for (Headers h : Headers.values()) {
      this.headers.add(h.toString());
    }
    memoryBefore = Util.getReallyUsedMemory();
  }

  public static <K> Map<K, Integer> sortByValues(final Map<K, Integer> map) {
    Comparator<K> valueComparator =
        new Comparator<K>() {
          public int compare(K k1, K k2) {
            if (map.get(k2) > map.get(k1)) return 1;
            else return -1;
          }
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
              if (!globalFieldTransitions.add(
                  new WeightedTransition<Field, INode<Node<Edge, Val>>, W>(t, w))) {
                fieldTransitionCollisions++;
              }
              fieldVisitedMethods.add(t.getStart().fact().stmt().getMethod());
              fieldVisitedStmts.add(t.getStart().fact().stmt());
              if (t.getLabel() instanceof ArrayField) {
                arrayFlows++;
              }
              addFieldGeneratedState(t.getStart());
              addFieldGeneratedState(t.getTarget());
            });

    solver
        .getCallAutomaton()
        .registerListener(
            (t, w, aut) -> {
              if (!globalCallTransitions.add(new WeightedTransition<>(t, w))) {
                callTransitionCollisions++;
              }
              callVisitedMethods.add(t.getLabel().getMethod());
              fieldVisitedStmts.add(t.getLabel());

              if (t.getStart().fact().isStatic()) {
                staticFlows++;
              }
              addCallGeneratedState(t.getStart());
              addCallGeneratedState(t.getTarget());
            });

    solver
        .getFieldPDS()
        .registerUpdateListener(
            rule -> {
              if (!globalFieldRules.add(rule)) {
                fieldRulesCollisions++;
              }
            });
    solver
        .getCallPDS()
        .registerUpdateListener(
            rule -> {
              if (!globalCallRules.add(rule)) {
                callRulesCollisions++;
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

  protected void addFieldGeneratedState(INode<Node<Edge, Val>> s) {
    if (s instanceof GeneratedState) {
      fieldGeneratedStates.add(s);
    }
  }

  protected void addCallGeneratedState(INode<Val> s) {
    if (s instanceof GeneratedState) {
      callGeneratedStates.add(s);
    }
  }

  @Override
  public void registerFieldWritePOI(WeightedBoomerang<W>.FieldWritePOI key) {
    fieldWritePOIs++;
  }

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
    s += computeMetrics();
    s += "\n";
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
  public void terminated(ForwardQuery query, ForwardBoomerangResults<W> res) {
    writeToFile(query, res.getAnalysisWatch().elapsed(TimeUnit.MILLISECONDS), res.isTimedout());
  }

  @Override
  public void terminated(BackwardQuery query, BackwardBoomerangResults<W> res) {
    writeToFile(query, res.getAnalysisWatch().elapsed(TimeUnit.MILLISECONDS), res.isTimedout());
  }

  private void writeToFile(Query query, long queryTime, boolean timeout) {
    long memoryAfter = Util.getReallyUsedMemory();
    put(Headers.Query, query.toString());
    put(Headers.QueryType, (query instanceof BackwardQuery ? "B" : "F"));
    put(Headers.QueryTime, queryTime);
    put(Headers.Timeout, (timeout ? "1" : "0"));
    put(Headers.ArrayFlows, arrayFlows);
    put(Headers.CallRules, globalCallRules.size());
    put(Headers.FieldRules, globalFieldRules.size());
    put(Headers.CallTransitions, globalCallTransitions.size());
    put(Headers.FieldTransitions, globalFieldTransitions.size());
    put(Headers.FieldReadPOIs, fieldReadPOIs);
    put(Headers.FieldWritePOIs, fieldWritePOIs);
    put(Headers.FieldVisitedMethods, fieldVisitedMethods.size());
    put(Headers.CallVisitedMethods, callVisitedMethods.size());
    put(Headers.FieldVisitedStmts, fieldVisitedStmts.size());
    put(Headers.CallVisitedStmts, callVisitedStmts.size());
    put(Headers.ReachedForwardNodes, reachedForwardNodes.size());
    put(Headers.ReachedBackwardNodes, reachedBackwardNodes.size());
    put(Headers.StaticFlows, staticFlows);
    // TODO implement
    //        put(Headers.ICFGEdges, Util.getICFGEdges());
    put(Headers.CallGeneratedStates, callGeneratedStates.size());
    put(Headers.FieldGeneratedStates, fieldGeneratedStates.size());
    put(
        Headers.CallLongestCallStack,
        queries.get(query).getCallAutomaton().getLongestPath().size());
    put(
        Headers.FieldLongestAccessPath,
        queries.get(query).getFieldAutomaton().getLongestPath().size());
    put(Headers.CallContainsLoop, queries.get(query).getCallAutomaton().containsLoop());
    put(Headers.FieldContainsLoop, queries.get(query).getFieldAutomaton().containsLoop());
    put(Headers.MemoryAfter, memoryAfter);
    put(Headers.MemoryBefore, memoryBefore);
    put(Headers.MemoryDiff, memoryAfter - memoryBefore);
    try {
      File reportFile = new File(outputFileName).getAbsoluteFile();
      if (!reportFile.getParentFile().exists()) {
        try {
          Files.createDirectories(reportFile.getParentFile().toPath());
        } catch (IOException e) {
          throw new RuntimeException("Was not able to create directories for IDEViz output!");
        }
      }
      boolean fileExisted = reportFile.exists();
      FileWriter writer = new FileWriter(reportFile, true);
      if (!fileExisted) {
        writer.write(Joiner.on(CSV_SEPARATOR).join(headers) + "\n");
      }
      List<String> line = Lists.newArrayList();
      for (String h : headers) {
        String string = headersToValues.get(h);
        if (string == null) {
          string = "";
        }
        line.add(string);
      }
      writer.write(Joiner.on(CSV_SEPARATOR).join(line) + "\n");
      writer.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void put(String key, Object val) {
    if (!headers.contains(key)) {
      System.err.println("Did not create a header to this value " + key);
    } else {
      headersToValues.put(key, val.toString());
    }
  }

  private void put(Headers key, Object val) {
    put(key.toString(), val);
  }
}
