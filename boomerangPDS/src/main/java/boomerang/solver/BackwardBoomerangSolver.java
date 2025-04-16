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
package boomerang.solver;

import boomerang.BackwardQuery;
import boomerang.callgraph.CalleeListener;
import boomerang.callgraph.ObservableICFG;
import boomerang.controlflowgraph.ObservableControlFlowGraph;
import boomerang.controlflowgraph.PredecessorListener;
import boomerang.controlflowgraph.SuccessorListener;
import boomerang.flowfunction.IBackwardFlowFunction;
import boomerang.options.BoomerangOptions;
import boomerang.scope.AllocVal;
import boomerang.scope.ControlFlowGraph;
import boomerang.scope.ControlFlowGraph.Edge;
import boomerang.scope.DataFlowScope;
import boomerang.scope.Field;
import boomerang.scope.InvokeExpr;
import boomerang.scope.Method;
import boomerang.scope.Statement;
import boomerang.scope.Type;
import boomerang.scope.Val;
import com.google.common.collect.Multimap;
import de.fraunhofer.iem.Location;
import java.util.AbstractMap.SimpleEntry;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sparse.SparseAliasingCFG;
import sparse.SparseCFGCache;
import sync.pds.solver.nodes.*;
import wpds.impl.NestedWeightedPAutomatons;
import wpds.impl.Transition;
import wpds.impl.Weight;
import wpds.interfaces.State;

public abstract class BackwardBoomerangSolver<W extends Weight> extends AbstractBoomerangSolver<W> {
  private static final Logger LOGGER = LoggerFactory.getLogger(BackwardBoomerangSolver.class);
  private final BackwardQuery query;
  private final IBackwardFlowFunction flowFunction;

  public BackwardBoomerangSolver(
      ObservableICFG<Statement, Method> icfg,
      ObservableControlFlowGraph cfg,
      Map<
              Entry<INode<Node<ControlFlowGraph.Edge, Val>>, Field>,
              INode<Node<ControlFlowGraph.Edge, Val>>>
          genField,
      BackwardQuery query,
      BoomerangOptions options,
      NestedWeightedPAutomatons<ControlFlowGraph.Edge, INode<Val>, W> callSummaries,
      NestedWeightedPAutomatons<Field, INode<Node<ControlFlowGraph.Edge, Val>>, W> fieldSummaries,
      DataFlowScope scope,
      IBackwardFlowFunction backwardFlowFunction,
      Multimap<Field, Statement> fieldLoadStatements,
      Multimap<Field, Statement> fieldStoreStatements,
      Type propagationType) {
    super(icfg, cfg, genField, options, callSummaries, fieldSummaries, scope, propagationType);
    this.query = query;
    this.flowFunction = backwardFlowFunction;
    this.flowFunction.setSolver(this, fieldLoadStatements, fieldStoreStatements);
  }

  private boolean notUsedInMethod(Method m, Statement curr, Val value) {
    if (value.isStatic()) {
      return false;
    }
    return m.getLocals().stream().noneMatch(local -> local.equals(value));
  }

  public INode<Node<ControlFlowGraph.Edge, Val>> generateFieldState(
      final INode<Node<ControlFlowGraph.Edge, Val>> d, final Field loc) {
    Entry<INode<Node<Edge, Val>>, Field> e = new SimpleEntry<>(d, loc);
    if (!generatedFieldState.containsKey(e)) {
      generatedFieldState.put(
          e, new GeneratedState<>(new SingleNode<>(new Node<>(epsilonStmt(), Val.zero())), loc));
    }
    return generatedFieldState.get(e);
  }

  /*
  @Override
  public INode<Val> generateCallState(INode<Val> d, Statement loc) {
    Entry<INode<Val>, Statement> e = new AbstractMap.SimpleEntry<>(d, loc);
    if (!generatedCallState.containsKey(e)) {
      generatedCallState.put(
          e, new GeneratedState<Val, Statement>(new SingleNode<Val>(Val.zero()), loc));
    }
    return generatedCallState.get(e);
  }
  */

  @Override
  protected Collection<? extends State> computeReturnFlow(
      Method method, Statement callerReturnStatement, Val value) {
    return flowFunction.returnFlow(method, callerReturnStatement, value).stream()
        .map(x -> new PopNode<>(x, PDSSystem.CALLS))
        .collect(Collectors.toCollection(LinkedHashSet::new));
  }

  protected void callFlow(Method caller, Node<Edge, Val> curr, Statement callSite) {
    InvokeExpr invokeExpr = callSite.getInvokeExpr();
    if (dataFlowScope.isExcluded(invokeExpr.getMethod())) {
      byPassFlowAtCallsite(caller, curr);
      return;
    }
    icfg.addCalleeListener(new CallSiteCalleeListener(curr, caller));
  }

  private void byPassFlowAtCallsite(Method caller, Node<Edge, Val> curr) {
    for (Statement returnSite :
        curr.stmt()
            .getStart()
            .getMethod()
            .getControlFlowGraph()
            .getPredsOf(curr.stmt().getStart())) {

      Collection<State> res =
          flowFunction.callToReturnFlow(new Edge(returnSite, curr.stmt().getStart()), curr.fact());
      for (State s : res) {
        propagate(curr, s);
      }
    }
  }

  @Override
  public void computeSuccessor(Node<Edge, Val> node) {
    LOGGER.trace("BW: Computing successor of {} for {}", node, this);
    Edge edge = node.stmt();
    Val value = node.fact();
    assert !(value instanceof AllocVal);
    Method method = edge.getStart().getMethod();
    if (method == null) return;
    if (dataFlowScope.isExcluded(method)) return;
    if (notUsedInMethod(method, edge.getStart(), value)) {
      return;
    }
    if (edge.getStart().containsInvokeExpr()
        && edge.getStart().uses(value)
        && INTERPROCEDURAL
        && checkSpecialInvoke(edge)) {
      callFlow(method, node, edge.getStart());
    } else if (icfg.isExitStmt(edge.getStart())) {
      returnFlow(method, node);
    } else {
      normalFlow(method, node);
    }
  }

  private boolean checkSpecialInvoke(Edge edge) {
    if (!options.handleSpecialInvokeAsNormalPropagation()) {
      return true;
    } else {
      return !edge.getStart().getInvokeExpr().isSpecialInvokeExpr();
    }
  }

  protected void normalFlow(Method method, Node<ControlFlowGraph.Edge, Val> currNode) {
    Edge curr = currNode.stmt();
    Val value = currNode.fact();

    /* TODO: [ms] re-enable  sparse + refactor if/else into own method!
    if (options.getSparsificationStrategy() != SparsificationStrategy.NONE) {
        propagateSparse(method, currNode, curr, value);
    } else */
    {
      for (Statement pred :
          curr.getStart().getMethod().getControlFlowGraph().getPredsOf(curr.getStart())) {
        Collection<State> flow = computeNormalFlow(method, new Edge(pred, curr.getStart()), value);
        for (State s : flow) {
          options.getSparsificationStrategy().getCounter().countBackwardProgragation();
          propagate(currNode, s);
        }
      }
    }
  }

  /*
  // TODO: [ms] re-enable sparse
    private void propagateSparse(Method method, Node<Edge, Val> currNode, Edge curr, Val value) {
      Statement propStmt = curr.getStart();
      SparseAliasingCFG sparseCFG = getSparseCFG(query, method, value, propStmt);
      Stmt stmt = SootAdapter.asStmt(propStmt);
      if (sparseCFG.getGraph().nodes().contains(stmt)) {
        Set<Unit> predecessors = sparseCFG.getGraph().predecessors(stmt);
        for (Unit pred : predecessors) {
          Collection<State> flow =
              computeNormalFlow(
                  method, new Edge(SootAdapter.asStatement(pred, method), propStmt), value);
          for (State s : flow) {
            options.getSparsificationStrategy().getCounter().countBackward();
            propagate(currNode, s);
          }
        }
      } else {
        System.out.println("node not in cfg:" + stmt);
      }
    }
  */

  /**
   * sparse BackwardQuery: (b2 (target.aliasing.Aliasing1.<target.aliasing.Aliasing1: void
   * main(java.lang.String[])>),b2.secret = $stack9 -> return)
   */
  private SparseAliasingCFG getSparseCFG(
      BackwardQuery query, Method method, Val val, Statement stmt) {
    SparseCFGCache sparseCFGCache =
        SparseCFGCache.getInstance(
            options.getSparsificationStrategy(), options.ignoreSparsificationAfterQuery());
    return sparseCFGCache.getSparseCFGForBackwardPropagation(
        query.var(), query.asNode().stmt().getStart(), method, val, stmt);
  }

  protected Collection<? extends State> computeCallFlow(
      Edge callSiteEdge, Val fact, Method callee, Edge calleeStartEdge) {
    Statement calleeSp = calleeStartEdge.getTarget();
    return flowFunction.callFlow(callSiteEdge.getTarget(), fact, callee, calleeSp).stream()
        .map(x -> new PushNode<>(calleeStartEdge, x, callSiteEdge, PDSSystem.CALLS))
        .collect(Collectors.toCollection(LinkedHashSet::new));
  }

  @Override
  public void processPush(
      Node<Edge, Val> curr, Location location, PushNode<Edge, Val, ?> succ, PDSSystem system) {
    if (PDSSystem.CALLS == system) {
      if (!((PushNode<Edge, Val, Edge>) succ).location().getTarget().equals(curr.stmt().getStart())
          || !(curr.stmt().getStart().containsInvokeExpr())) {
        throw new RuntimeException("Invalid push rule");
      }
    }
    super.processPush(curr, location, succ, system);
  }

  @Override
  protected Collection<State> computeNormalFlow(Method method, Edge currEdge, Val fact) {
    return flowFunction.normalFlow(currEdge, fact);
  }

  @Override
  public void applyCallSummary(
      Edge callSiteEdge, Val factAtSpInCallee, Edge spInCallee, Edge exitStmt, Val exitingFact) {
    Set<Node<Edge, Val>> out = new LinkedHashSet<>();
    Statement callSite = callSiteEdge.getTarget();
    if (callSite.containsInvokeExpr()) {
      if (exitingFact.isThisLocal()) {
        if (callSite.getInvokeExpr().isInstanceInvokeExpr()) {
          out.add(new Node<>(callSiteEdge, callSite.getInvokeExpr().getBase()));
        }
      }
      if (exitingFact.isReturnLocal()) {
        if (callSite.isAssignStmt()) {
          out.add(new Node<>(callSiteEdge, callSite.getLeftOp()));
        }
      }
      for (int i = 0; i < callSite.getInvokeExpr().getArgs().size(); i++) {
        if (exitingFact.isParameterLocal(i)) {
          out.add(new Node<>(callSiteEdge, callSite.getInvokeExpr().getArg(i)));
        }
      }
    }
    for (Node<Edge, Val> xs : out) {
      addNormalCallFlow(new Node<>(callSiteEdge, exitingFact), xs);
      addNormalFieldFlow(new Node<>(exitStmt, exitingFact), xs);
    }
  }

  @Override
  protected void propagateUnbalancedToCallSite(
      Statement callSite, Transition<Edge, INode<Val>> transInCallee) {
    GeneratedState<Val, Edge> target = (GeneratedState<Val, Edge>) transInCallee.getTarget();

    if (!callSite.containsInvokeExpr()) {
      throw new RuntimeException("Invalid propagate Unbalanced return");
    }
    if (!isMatchingCallSiteCalleePair(callSite, transInCallee.getLabel().getMethod())) {
      return;
    }
    cfg.addSuccsOfListener(
        new SuccessorListener(callSite) {
          @Override
          public void getSuccessor(Statement succ) {
            cfg.addPredsOfListener(
                new PredecessorListener(callSite) {
                  @Override
                  public void getPredecessor(Statement pred) {
                    Node<ControlFlowGraph.Edge, Val> curr =
                        new Node<>(new Edge(callSite, succ), query.var());

                    Transition<ControlFlowGraph.Edge, INode<Val>> callTrans =
                        new Transition<>(
                            wrap(curr.fact()),
                            curr.stmt(),
                            generateCallState(wrap(curr.fact()), curr.stmt()));
                    callAutomaton.addTransition(callTrans);
                    callAutomaton.addUnbalancedState(
                        generateCallState(wrap(curr.fact()), curr.stmt()), target);

                    State s =
                        new PushNode<>(
                            target.location(),
                            target.node().fact(),
                            new Edge(pred, callSite),
                            PDSSystem.CALLS);
                    propagate(curr, s);
                  }
                });
          }
        });
  }

  private final class CallSiteCalleeListener implements CalleeListener<Statement, Method> {
    private final Statement callSite;
    private final Node<Edge, Val> curr;
    private final Method caller;

    private CallSiteCalleeListener(Node<Edge, Val> curr, Method caller) {
      this.curr = curr;
      this.callSite = curr.stmt().getStart();
      this.caller = caller;
    }

    @Override
    public Statement getObservedCaller() {
      return callSite;
    }

    @Override
    public void onCalleeAdded(Statement callSite, Method callee) {
      if (callee.isStaticInitializer()) {
        return;
      }
      for (Statement calleeSp : icfg.getStartPointsOf(callee)) {
        for (Statement predOfCall :
            callSite.getMethod().getControlFlowGraph().getPredsOf(callSite)) {
          Collection<? extends State> res =
              computeCallFlow(
                  new Edge(predOfCall, callSite),
                  curr.fact(),
                  callee,
                  new Edge(calleeSp, calleeSp));
          for (State o : res) {
            BackwardBoomerangSolver.this.propagate(curr, o);
          }
        }
      }
    }

    @Override
    public void onNoCalleeFound() {
      byPassFlowAtCallsite(caller, curr);
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + getOuterType().hashCode();
      result = prime * result + ((caller == null) ? 0 : caller.hashCode());
      result = prime * result + ((curr == null) ? 0 : curr.hashCode());
      return result;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null) return false;
      if (getClass() != obj.getClass()) return false;
      CallSiteCalleeListener other = (CallSiteCalleeListener) obj;
      if (!getOuterType().equals(other.getOuterType())) return false;
      if (caller == null) {
        if (other.caller != null) return false;
      } else if (!caller.equals(other.caller)) return false;
      if (curr == null) {
        return other.curr == null;
      } else return curr.equals(other.curr);
    }

    private BackwardBoomerangSolver getOuterType() {
      return BackwardBoomerangSolver.this;
    }
  }

  @Override
  public String toString() {
    return "BackwardBoomerangSolver{" + "query=" + query + '}';
  }
}
