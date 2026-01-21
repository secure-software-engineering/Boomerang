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

import boomerang.ForwardQuery;
import boomerang.Util;
import boomerang.callgraph.CallerListener;
import boomerang.callgraph.ObservableICFG;
import boomerang.controlflowgraph.ObservableControlFlowGraph;
import boomerang.controlflowgraph.PredecessorListener;
import boomerang.scope.ControlFlowGraph;
import boomerang.scope.DeclaredMethod;
import boomerang.scope.Method;
import boomerang.scope.Statement;
import boomerang.scope.Val;
import boomerang.scope.fields.EmptyField;
import boomerang.solver.AbstractBoomerangSolver;
import boomerang.solver.ForwardBoomerangSolver;
import boomerang.stats.IBoomerangStats;
import boomerang.util.DefaultValueMap;
import com.google.common.base.Stopwatch;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;
import sync.pds.solver.nodes.GeneratedState;
import sync.pds.solver.nodes.Node;
import wpds.impl.Weight;
import wpds.interfaces.State;

public class ForwardBoomerangResults<W extends Weight> extends AbstractBoomerangResults<W> {

  private final ForwardQuery query;
  private final boolean timedOut;
  private final IBoomerangStats<W> stats;
  private final Stopwatch analysisWatch;
  private final long maxMemory;
  private final ObservableICFG<Statement, Method> icfg;
  private final Collection<Method> visitedMethods;
  private final ObservableControlFlowGraph cfg;

  public ForwardBoomerangResults(
      ForwardQuery query,
      ObservableICFG<Statement, Method> icfg,
      ObservableControlFlowGraph cfg,
      boolean timedOut,
      DefaultValueMap<ForwardQuery, ForwardBoomerangSolver<W>> queryToSolvers,
      IBoomerangStats<W> stats,
      Stopwatch analysisWatch,
      Collection<Method> visitedMethods) {
    super(queryToSolvers);
    this.query = query;
    this.icfg = icfg;
    this.cfg = cfg;
    this.timedOut = timedOut;
    this.stats = stats;
    this.analysisWatch = analysisWatch;
    this.visitedMethods = visitedMethods;
    stats.terminated(query, this);
    this.maxMemory = Util.getReallyUsedMemory();
  }

  public Stopwatch getAnalysisWatch() {
    return analysisWatch;
  }

  public boolean isTimedOut() {
    return timedOut;
  }

  public Table<ControlFlowGraph.Edge, Val, W> asEdgeValWeightTable() {
    return asEdgeValWeightTable(query);
  }

  public Table<Statement, Val, W> asStatementValWeightTable() {
    return asStatementValWeightTable(query);
  }

  /**
   * Computes the final weights for the seed. The weights correspond to weight at the last
   * statements within the method where the object is alive.
   *
   * @return a table that maps the last statements to the final existing aliases and there final
   *     weights
   */
  public Table<Statement, Val, W> computeFinalWeights() {
    ForwardBoomerangSolver<W> solver = queryToSolvers.get(query);
    if (solver == null) {
      return HashBasedTable.create();
    }

    Table<Statement, Val, W> table = asStatementValWeightTable();
    Collection<Method> visitedMethods = new LinkedHashSet<>();
    for (Statement statement : table.rowKeySet()) {
      visitedMethods.add(statement.getMethod());
    }

    Table<Statement, Val, W> lastWeights = HashBasedTable.create();
    for (Method flowReaches : visitedMethods) {
      for (Statement exitStmt : icfg.getEndPointsOf(flowReaches)) {
        Collection<State> escapeNodes = new LinkedHashSet<>();

        icfg.addCallerListener(
            new CallerListener<>() {
              @Override
              public Method getObservedCallee() {
                return flowReaches;
              }

              @Override
              public void onCallerAdded(Statement callSite, Method callee) {
                Method method = callSite.getMethod();
                if (visitedMethods.contains(method)) {
                  for (Val factAtReturn : table.row(exitStmt).keySet()) {
                    Collection<? extends State> returnNodes =
                        solver.computeReturnFlow(flowReaches, exitStmt, factAtReturn);
                    escapeNodes.addAll(returnNodes);
                  }
                }
              }
            });

        if (escapeNodes.isEmpty()) {
          Collection<Statement> lastStatements = computeFinalStatements(exitStmt, table);
          for (Statement lastStmt : lastStatements) {
            Map<Val, W> finalWeights = table.row(lastStmt);

            for (Map.Entry<Val, W> entry : finalWeights.entrySet()) {
              lastWeights.put(lastStmt, entry.getKey(), entry.getValue());
            }
          }
        }
      }
    }

    return lastWeights;
  }

  private Collection<Statement> computeFinalStatements(
      Statement returnSite, Table<Statement, Val, W> table) {
    if (table.containsRow(returnSite)) {
      return Collections.singleton(returnSite);
    }

    Collection<Statement> finalStatements = new HashSet<>();
    Queue<Statement> workList = new LinkedList<>();
    workList.add(returnSite);

    Collection<Statement> visited = new HashSet<>();

    while (!workList.isEmpty()) {
      Statement currStmt = workList.poll();

      if (!visited.add(currStmt)) {
        continue;
      }

      boolean added = false;
      if (table.containsRow(currStmt)) {
        finalStatements.add(currStmt);
        added = true;
      }

      if (!added && !currStmt.isIdentityStmt()) {
        cfg.addPredsOfListener(
            new PredecessorListener(currStmt) {

              @Override
              public void getPredecessor(Statement succ) {
                workList.add(succ);
              }
            });
      }
    }

    return finalStatements;
  }

  public Table<ControlFlowGraph.Edge, Val, W> getObjectDestructingStatements() {
    AbstractBoomerangSolver<W> solver = queryToSolvers.get(query);
    if (solver == null) {
      return HashBasedTable.create();
    }
    Table<ControlFlowGraph.Edge, Val, W> res = asEdgeValWeightTable();
    Set<Method> visitedMethods = new LinkedHashSet<>();
    for (ControlFlowGraph.Edge s : res.rowKeySet()) {
      visitedMethods.add(s.getMethod());
    }
    ForwardBoomerangSolver<W> forwardSolver = queryToSolvers.get(query);
    Table<ControlFlowGraph.Edge, Val, W> destructingStatement = HashBasedTable.create();
    for (Method flowReaches : visitedMethods) {
      for (Statement exitStmt : icfg.getEndPointsOf(flowReaches)) {
        for (Statement predOfExit :
            exitStmt.getMethod().getControlFlowGraph().getPredsOf(exitStmt)) {
          ControlFlowGraph.Edge exitEdge = new ControlFlowGraph.Edge(predOfExit, exitStmt);
          Set<State> escapes = new LinkedHashSet<>();
          icfg.addCallerListener(
              new CallerListener<>() {
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
            findLastUsage(exitEdge, row, destructingStatement);
          }
        }
      }
    }

    return destructingStatement;
  }

  private void findLastUsage(
      ControlFlowGraph.Edge exitStmt,
      Map<Val, W> row,
      Table<ControlFlowGraph.Edge, Val, W> destructingStatement) {
    LinkedList<ControlFlowGraph.Edge> worklist = Lists.newLinkedList();
    worklist.add(exitStmt);
    Set<ControlFlowGraph.Edge> visited = new LinkedHashSet<>();
    while (!worklist.isEmpty()) {
      ControlFlowGraph.Edge curr = worklist.poll();
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
          /* Do not continue over CatchStmt */
          !(curr.getTarget().isIdentityStmt())) {
        cfg.addPredsOfListener(
            new PredecessorListener(curr.getStart()) {

              @Override
              public void getPredecessor(Statement succ) {
                worklist.add(new ControlFlowGraph.Edge(succ, curr.getStart()));
              }
            });
      }
    }
  }

  public IBoomerangStats<W> getStats() {
    return stats;
  }

  public Map<ControlFlowGraph.Edge, DeclaredMethod> getInvokedMethodOnInstance() {
    Map<ControlFlowGraph.Edge, DeclaredMethod> invokedMethodsOnInstance = Maps.newHashMap();
    if (query.cfgEdge().getStart().containsInvokeExpr()) {
      invokedMethodsOnInstance.put(
          query.cfgEdge(), query.cfgEdge().getStart().getInvokeExpr().getDeclaredMethod());
    }
    queryToSolvers
        .get(query)
        .getFieldAutomaton()
        .registerListener(
            (t, w, aut) -> {
              if (!t.getLabel().equals(EmptyField.getInstance())
                  || t.getStart() instanceof GeneratedState) {
                return;
              }
              Node<ControlFlowGraph.Edge, Val> node = t.getStart().fact();
              Val fact = node.fact();
              ControlFlowGraph.Edge currEdge = node.stmt();
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

    Map<ControlFlowGraph.Edge, DeclaredMethod> callsOnObject = getInvokedMethodOnInstance();
    for (ControlFlowGraph.Edge edge : callsOnObject.keySet()) {
      statements.add(edge.getStart());
    }

    return statements;
  }

  public Context getContext(Node<ControlFlowGraph.Edge, Val> node) {
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

  public Collection<Method> getVisitedMethods() {
    return visitedMethods;
  }

  public long getMaxMemory() {
    return maxMemory;
  }
}
