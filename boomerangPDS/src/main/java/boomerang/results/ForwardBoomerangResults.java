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
package boomerang.results;

import boomerang.ForwardQuery;
import boomerang.Util;
import boomerang.callgraph.CallerListener;
import boomerang.callgraph.ObservableICFG;
import boomerang.controlflowgraph.ObservableControlFlowGraph;
import boomerang.controlflowgraph.PredecessorListener;
import boomerang.scope.ControlFlowGraph.Edge;
import boomerang.scope.DeclaredMethod;
import boomerang.scope.Field;
import boomerang.scope.FrameworkScope;
import boomerang.scope.IfStatement;
import boomerang.scope.IfStatement.Evaluation;
import boomerang.scope.Method;
import boomerang.scope.Statement;
import boomerang.scope.Val;
import boomerang.solver.AbstractBoomerangSolver;
import boomerang.solver.ForwardBoomerangSolver;
import boomerang.stats.IBoomerangStats;
import boomerang.util.DefaultValueMap;
import boomerang.weights.DataFlowPathWeight;
import boomerang.weights.PathConditionWeight.ConditionDomain;
import com.google.common.base.Stopwatch;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import sync.pds.solver.nodes.GeneratedState;
import sync.pds.solver.nodes.INode;
import sync.pds.solver.nodes.Node;
import wpds.impl.Transition;
import wpds.impl.Weight;
import wpds.impl.WeightedPAutomaton;
import wpds.interfaces.State;

public class ForwardBoomerangResults<W extends Weight> extends AbstractBoomerangResults<W> {

  private final FrameworkScope scopeFactory;
  private final ForwardQuery query;
  private final boolean timedout;
  private final IBoomerangStats<W> stats;
  private final Stopwatch analysisWatch;
  private final long maxMemory;
  private final ObservableICFG<Statement, Method> icfg;
  private final Set<Method> visitedMethods;
  private final boolean trackDataFlowPath;
  private final boolean pruneContradictoryDataFlowPath;
  private final ObservableControlFlowGraph cfg;
  private final boolean pruneImplictFlows;

  public ForwardBoomerangResults(
      FrameworkScope scopeFactory,
      ForwardQuery query,
      ObservableICFG<Statement, Method> icfg,
      ObservableControlFlowGraph cfg,
      boolean timedout,
      DefaultValueMap<ForwardQuery, ForwardBoomerangSolver<W>> queryToSolvers,
      IBoomerangStats<W> stats,
      Stopwatch analysisWatch,
      Set<Method> visitedMethods,
      boolean trackDataFlowPath,
      boolean pruneContradictoryDataFlowPath,
      boolean pruneImplictFlows) {
    super(queryToSolvers);
    this.scopeFactory = scopeFactory;
    this.query = query;
    this.icfg = icfg;
    this.cfg = cfg;
    this.timedout = timedout;
    this.stats = stats;
    this.analysisWatch = analysisWatch;
    this.visitedMethods = visitedMethods;
    this.trackDataFlowPath = trackDataFlowPath;
    this.pruneContradictoryDataFlowPath = pruneContradictoryDataFlowPath;
    this.pruneImplictFlows = pruneImplictFlows;
    stats.terminated(query, this);
    this.maxMemory = Util.getReallyUsedMemory();
  }

  public Stopwatch getAnalysisWatch() {
    return analysisWatch;
  }

  public boolean isTimedout() {
    return timedout;
  }

  public Table<Edge, Val, W> getObjectDestructingStatements() {
    AbstractBoomerangSolver<W> solver = queryToSolvers.get(query);
    if (solver == null) {
      return HashBasedTable.create();
    }
    Table<Edge, Val, W> res = asEdgeValWeightTable();
    Set<Method> visitedMethods = new LinkedHashSet<>();
    for (Edge s : res.rowKeySet()) {
      visitedMethods.add(s.getMethod());
    }
    ForwardBoomerangSolver<W> forwardSolver = queryToSolvers.get(query);
    Table<Edge, Val, W> destructingStatement = HashBasedTable.create();
    for (Method flowReaches : visitedMethods) {
      for (Statement exitStmt : icfg.getEndPointsOf(flowReaches)) {
        for (Statement predOfExit :
            exitStmt.getMethod().getControlFlowGraph().getPredsOf(exitStmt)) {
          Edge exitEdge = new Edge(predOfExit, exitStmt);
          Set<State> escapes = new LinkedHashSet<>();
          icfg.addCallerListener(
              new CallerListener<Statement, Method>() {
                @Override
                public Method getObservedCallee() {
                  return flowReaches;
                }

                @Override
                public void onCallerAdded(Statement callSite, Method m) {
                  Method callee = callSite.getMethod();
                  if (visitedMethods.contains(callee)) {
                    for (Entry<Val, W> valAndW : res.row(exitEdge).entrySet()) {
                      escapes.addAll(
                          forwardSolver.computeReturnFlow(flowReaches, exitStmt, valAndW.getKey()));
                    }
                  }
                }
              });

          if (escapes.isEmpty()) {
            Map<Val, W> row = res.row(exitEdge);
            findLastUsage(exitEdge, row, destructingStatement, forwardSolver);
          }
        }
      }
    }

    return destructingStatement;
  }

  public Table<Edge, Val, W> asEdgeValWeightTable() {
    return asEdgeValWeightTable(query);
  }

  public Table<Statement, Val, W> asStatementValWeightTable() {
    return asStatementValWeightTable(query);
  }

  private void findLastUsage(
      Edge exitStmt,
      Map<Val, W> row,
      Table<Edge, Val, W> destructingStatement,
      ForwardBoomerangSolver<W> forwardSolver) {
    LinkedList<Edge> worklist = Lists.newLinkedList();
    worklist.add(exitStmt);
    Set<Edge> visited = new LinkedHashSet<>();
    while (!worklist.isEmpty()) {
      Edge curr = worklist.poll();
      if (!visited.add(curr)) {
        continue;
      }
      boolean valueUsedInStmt = false;
      for (Entry<Val, W> e : row.entrySet()) {
        if (curr.getTarget().uses(e.getKey())) {
          destructingStatement.put(curr, e.getKey(), e.getValue());
          valueUsedInStmt = true;
        }
      }
      if (!valueUsedInStmt
          &&
          /** Do not continue over CatchStmt */
          !(curr.getTarget().isIdentityStmt())) {
        cfg.addPredsOfListener(
            new PredecessorListener(curr.getStart()) {

              @Override
              public void getPredecessor(Statement succ) {
                worklist.add(new Edge(succ, curr.getStart()));
              }
            });
      }
    }
  }

  public IBoomerangStats<W> getStats() {
    return stats;
  }

  public Map<Edge, DeclaredMethod> getInvokedMethodOnInstance() {
    Map<Edge, DeclaredMethod> invokedMethodsOnInstance = Maps.newHashMap();
    if (query.cfgEdge().getStart().containsInvokeExpr()) {
      invokedMethodsOnInstance.put(
          query.cfgEdge(), query.cfgEdge().getStart().getInvokeExpr().getDeclaredMethod());
    }
    queryToSolvers
        .get(query)
        .getFieldAutomaton()
        .registerListener(
            (t, w, aut) -> {
              if (!t.getLabel().equals(Field.empty()) || t.getStart() instanceof GeneratedState) {
                return;
              }
              Node<Edge, Val> node = t.getStart().fact();
              Val fact = node.fact();
              Edge currEdge = node.stmt();
              Statement curr = currEdge.getStart();
              if (curr.containsInvokeExpr()) {
                if (curr.getInvokeExpr().isInstanceInvokeExpr()) {
                  Val base = curr.getInvokeExpr().getBase();
                  if (base.equals(fact)) {
                    invokedMethodsOnInstance.put(
                        currEdge, curr.getInvokeExpr().getDeclaredMethod());
                  }
                }
              }
            });
    return invokedMethodsOnInstance;
  }

  /**
   * Get all statements that contain an invoke expression belonging to the original seed.
   *
   * @return the statements that contain invoke expressions belonging to the original seed.
   */
  public Collection<Statement> getInvokeStatementsOnInstance() {
    Collection<Statement> statements = new HashSet<>();

    Map<Edge, DeclaredMethod> callsOnObject = getInvokedMethodOnInstance();
    for (Edge edge : callsOnObject.keySet()) {
      statements.add(edge.getStart());
    }

    return statements;
  }

  public QueryResults getPotentialNullPointerDereferences() {
    // FIXME this should be located nullpointer analysis
    Set<Node<Edge, Val>> res = new LinkedHashSet<>();
    for (Transition<Field, INode<Node<Edge, Val>>> t :
        queryToSolvers.get(query).getFieldAutomaton().getTransitions()) {
      if (!t.getLabel().equals(Field.empty()) || t.getStart() instanceof GeneratedState) {
        continue;
      }
      Node<Edge, Val> nullPointerNode = t.getStart().fact();
      if (NullPointerDereference.isNullPointerNode(nullPointerNode)
          && queryToSolvers.get(query).getReachedStates().contains(nullPointerNode)) {
        res.add(nullPointerNode);
      }
    }
    Set<AffectedLocation> resWithContext = new LinkedHashSet<>();
    for (Node<Edge, Val> r : res) {
      // Context context = constructContextGraph(query, r);
      if (trackDataFlowPath) {
        DataFlowPathWeight dataFlowPath = getDataFlowPathWeight(query, r);
        if (isValidPath(dataFlowPath)) {
          List<PathElement> p = transformPath(dataFlowPath.getAllStatements(), r);
          resWithContext.add(new NullPointerDereference(query, r.stmt(), r.fact(), null, null, p));
        }
      } else {
        List<PathElement> dataFlowPath = Lists.newArrayList();
        resWithContext.add(
            new NullPointerDereference(query, r.stmt(), r.fact(), null, null, dataFlowPath));
      }
    }
    QueryResults nullPointerResult =
        new QueryResults(query, resWithContext, visitedMethods, timedout);
    return nullPointerResult;
  }

  private boolean isValidPath(DataFlowPathWeight dataFlowPath) {
    if (!pruneContradictoryDataFlowPath) {
      return true;
    }
    Map<Statement, ConditionDomain> conditions = dataFlowPath.getConditions();
    for (Entry<Statement, ConditionDomain> c : conditions.entrySet()) {
      if (contradiction(c.getKey(), c.getValue(), dataFlowPath.getEvaluationMap())) {
        return false;
      }
    }
    return true;
  }

  private DataFlowPathWeight getDataFlowPathWeight(
      ForwardQuery query, Node<Edge, Val> sinkLocation) {
    WeightedPAutomaton<Edge, INode<Val>, W> callAut =
        queryToSolvers.getOrCreate(query).getCallAutomaton();
    // Iterating over whole set to find the matching transition is not the most elegant solution....
    for (Entry<Transition<Edge, INode<Val>>, W> e :
        callAut.getTransitionsToFinalWeights().entrySet()) {
      Transition<Edge, INode<Val>> t = e.getKey();
      if (t.getLabel().equals(new Edge(Statement.epsilon(), Statement.epsilon()))) {
        continue;
      }
      if (t.getStart().fact().isLocal()
          && !t.getLabel().getMethod().equals(t.getStart().fact().m())) {
        continue;
      }
      if (t.getStart().fact().equals(sinkLocation.fact())
          && t.getLabel().equals(sinkLocation.stmt())) {
        if (e.getValue() instanceof DataFlowPathWeight) {
          DataFlowPathWeight v = (DataFlowPathWeight) e.getValue();
          return v;
        }
      }
    }
    return null;
  }

  private boolean contradiction(
      Statement ifStmt, ConditionDomain mustBeVal, Map<Val, ConditionDomain> evaluationMap) {
    if (ifStmt.isIfStmt()) {
      IfStatement ifStmt1 = ifStmt.getIfStmt();
      for (Transition<Field, INode<Node<Edge, Val>>> t :
          queryToSolvers.get(query).getFieldAutomaton().getTransitions()) {

        if (!t.getStart().fact().stmt().equals(ifStmt)) {
          continue;
        }
        if (!t.getLabel().equals(Field.empty()) || t.getStart() instanceof GeneratedState) {
          continue;
        }

        Node<Edge, Val> node = t.getStart().fact();
        Val fact = node.fact();
        switch (ifStmt1.evaluate(fact)) {
          case TRUE:
            if (mustBeVal.equals(ConditionDomain.FALSE)) {
              return true;
            }
            break;
          case FALSE:
            if (mustBeVal.equals(ConditionDomain.TRUE)) {
              return true;
            }
        }
      }
      if (pruneImplictFlows) {
        for (Entry<Val, ConditionDomain> e : evaluationMap.entrySet()) {

          Val key = e.getKey();
          if (ifStmt1.uses(key)) {
            Evaluation eval = null;
            if (e.getValue().equals(ConditionDomain.TRUE)) {
              // Map first to JimpleVal
              eval = ifStmt1.evaluate(scopeFactory.getTrueValue(key.m()));
            } else if (e.getValue().equals(ConditionDomain.FALSE)) {
              // Map first to JimpleVal
              eval = ifStmt1.evaluate(scopeFactory.getFalseValue(key.m()));
            }
            if (eval != null) {
              if (mustBeVal.equals(ConditionDomain.FALSE)) {
                if (eval.equals(Evaluation.FALSE)) {
                  return true;
                }
              } else if (mustBeVal.equals(ConditionDomain.TRUE)) {
                if (eval.equals(Evaluation.TRUE)) {
                  return true;
                }
              }
            }
          }
        }
      }
    }
    return false;
  }

  private List<PathElement> transformPath(
      List<Node<Edge, Val>> allStatements, Node<Edge, Val> sinkLocation) {
    List<PathElement> res = Lists.newArrayList();
    int index = 0;
    for (Node<Edge, Val> x : allStatements) {
      res.add(new PathElement(x.stmt(), x.fact(), index++));
    }
    // TODO The analysis misses
    if (!allStatements.contains(sinkLocation)) {
      res.add(new PathElement(sinkLocation.stmt(), sinkLocation.fact(), index));
    }

    for (PathElement n : res) {
      LOGGER.trace(
          "Statement: {}, Variable {}, Index {}", n.getEdge(), n.getVariable(), n.stepIndex());
    }
    return res;
  }

  public Context getContext(Node<Edge, Val> node) {
    return constructContextGraph(query, node);
  }

  public boolean containsCallRecursion() {
    for (Entry<ForwardQuery, ForwardBoomerangSolver<W>> e : queryToSolvers.entrySet()) {
      if (e.getValue().getCallAutomaton().containsLoop()) {
        return true;
      }
    }
    return false;
  }

  public boolean containsFieldLoop() {
    for (Entry<ForwardQuery, ForwardBoomerangSolver<W>> e : queryToSolvers.entrySet()) {
      if (e.getValue().getFieldAutomaton().containsLoop()) {
        return true;
      }
    }
    return false;
  }

  public Set<Method> getVisitedMethods() {
    return visitedMethods;
  }

  public long getMaxMemory() {
    return maxMemory;
  }
}
