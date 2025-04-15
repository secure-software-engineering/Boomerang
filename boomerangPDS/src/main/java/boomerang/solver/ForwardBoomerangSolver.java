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

import boomerang.ForwardQuery;
import boomerang.Query;
import boomerang.callgraph.CalleeListener;
import boomerang.callgraph.ObservableICFG;
import boomerang.controlflowgraph.*;
import boomerang.flowfunction.IForwardFlowFunction;
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
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.LoggerFactory;
import sync.pds.solver.nodes.GeneratedState;
import sync.pds.solver.nodes.INode;
import sync.pds.solver.nodes.Node;
import sync.pds.solver.nodes.PopNode;
import sync.pds.solver.nodes.PushNode;
import sync.pds.solver.nodes.SingleNode;
import wpds.impl.NestedWeightedPAutomatons;
import wpds.impl.Transition;
import wpds.impl.Weight;
import wpds.impl.WeightedPAutomaton;
import wpds.interfaces.State;
import wpds.interfaces.WPAStateListener;

public abstract class ForwardBoomerangSolver<W extends Weight> extends AbstractBoomerangSolver<W> {
  private static final org.slf4j.Logger LOGGER =
      LoggerFactory.getLogger(ForwardBoomerangSolver.class);
  private final ForwardQuery query;
  private final IForwardFlowFunction flowFunctions;

  public ForwardBoomerangSolver(
      ObservableICFG<Statement, Method> callGraph,
      ObservableControlFlowGraph cfg,
      ForwardQuery query,
      Map<Entry<INode<Node<Edge, Val>>, Field>, INode<Node<Edge, Val>>> genField,
      BoomerangOptions options,
      NestedWeightedPAutomatons<Edge, INode<Val>, W> callSummaries,
      NestedWeightedPAutomatons<Field, INode<Node<Edge, Val>>, W> fieldSummaries,
      DataFlowScope scope,
      IForwardFlowFunction flowFunctions,
      Multimap<Field, Statement> fieldLoadStatements,
      Multimap<Field, Statement> fieldStoreStatements,
      Type propagationType) {
    super(callGraph, cfg, genField, options, callSummaries, fieldSummaries, scope, propagationType);
    this.query = query;
    this.flowFunctions = flowFunctions;
    this.flowFunctions.setSolver(this, fieldLoadStatements, fieldStoreStatements);
  }

  @Override
  public void processPush(
      Node<Edge, Val> curr, Location location, PushNode<Edge, Val, ?> succ, PDSSystem system) {
    if (PDSSystem.CALLS == system) {
      if (!((PushNode<Edge, Val, Edge>) succ).location().getStart().equals(curr.stmt().getTarget())
          || !curr.stmt().getTarget().containsInvokeExpr()) {
        throw new RuntimeException("Invalid push rule");
      }
    }
    super.processPush(curr, location, succ, system);
  }

  private final class OverwriteAtFieldStore
      extends WPAStateListener<Field, INode<Node<ControlFlowGraph.Edge, Val>>, W> {
    private final Edge nextStmt;

    private OverwriteAtFieldStore(INode<Node<Edge, Val>> state, Edge nextEdge) {
      super(state);
      this.nextStmt = nextEdge;
    }

    @Override
    public void onOutTransitionAdded(
        Transition<Field, INode<Node<ControlFlowGraph.Edge, Val>>> t,
        W w,
        WeightedPAutomaton<Field, INode<Node<Edge, Val>>, W> weightedPAutomaton) {
      if (t.getLabel().equals(nextStmt.getTarget().getFieldStore().getY())) {
        LOGGER.trace("Overwriting field {} at {}", t.getLabel(), nextStmt);
        overwriteFieldAtStatement(nextStmt, t);
      }
    }

    @Override
    public void onInTransitionAdded(
        Transition<Field, INode<Node<ControlFlowGraph.Edge, Val>>> t,
        W w,
        WeightedPAutomaton<Field, INode<Node<Edge, Val>>, W> weightedPAutomaton) {}

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + getEnclosingInstance().hashCode();
      result = prime * result + ((nextStmt == null) ? 0 : nextStmt.hashCode());
      return result;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) return true;
      if (!super.equals(obj)) return false;
      if (getClass() != obj.getClass()) return false;
      OverwriteAtFieldStore other = (OverwriteAtFieldStore) obj;
      if (!getEnclosingInstance().equals(other.getEnclosingInstance())) return false;
      if (nextStmt == null) {
        return other.nextStmt == null;
      } else return nextStmt.equals(other.nextStmt);
    }

    private ForwardBoomerangSolver getEnclosingInstance() {
      return ForwardBoomerangSolver.this;
    }
  }

  private final class OverwriteAtArrayStore
      extends WPAStateListener<Field, INode<Node<Edge, Val>>, W> {
    private final Edge nextStmt;

    private OverwriteAtArrayStore(INode<Node<Edge, Val>> state, Edge nextStmt) {
      super(state);
      this.nextStmt = nextStmt;
    }

    @Override
    public void onOutTransitionAdded(
        Transition<Field, INode<Node<Edge, Val>>> t,
        W w,
        WeightedPAutomaton<Field, INode<Node<Edge, Val>>, W> weightedPAutomaton) {
      if (t.getLabel().equals(Field.array(nextStmt.getTarget().getArrayBase().getY()))) {
        LOGGER.trace("Overwriting field {} at {}", t.getLabel(), nextStmt);
        overwriteFieldAtStatement(nextStmt, t);
      }
    }

    @Override
    public void onInTransitionAdded(
        Transition<Field, INode<Node<Edge, Val>>> t,
        W w,
        WeightedPAutomaton<Field, INode<Node<Edge, Val>>, W> weightedPAutomaton) {}

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + getEnclosingInstance().hashCode();
      result = prime * result + ((nextStmt == null) ? 0 : nextStmt.hashCode());
      return result;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) return true;
      if (!super.equals(obj)) return false;
      if (getClass() != obj.getClass()) return false;
      OverwriteAtArrayStore other = (OverwriteAtArrayStore) obj;
      if (!getEnclosingInstance().equals(other.getEnclosingInstance())) return false;
      if (nextStmt == null) {
        return other.nextStmt == null;
      } else return nextStmt.equals(other.nextStmt);
    }

    private ForwardBoomerangSolver getEnclosingInstance() {
      return ForwardBoomerangSolver.this;
    }
  }

  @Override
  protected void propagateUnbalancedToCallSite(
      Statement callSite, Transition<ControlFlowGraph.Edge, INode<Val>> transInCallee) {
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
                        new Node<>(new Edge(pred, callSite), query.var());
                    /*
                     * Transition<Field, INode<Node<Statement, Val>>> fieldTrans = new
                     * Transition<>(new SingleNode<>(curr), emptyField(), new SingleNode<>(curr));
                     * fieldAutomaton.addTransition(fieldTrans);*
                     */
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
                            new Edge(callSite, succ),
                            PDSSystem.CALLS);
                    propagate(curr, s);
                  }
                });
          }
        });
  }

  private final class CallSiteCalleeListener implements CalleeListener<Statement, Method> {
    private final Method caller;
    private final Statement callSite;
    private final Edge callSiteEdge;
    private final Node<ControlFlowGraph.Edge, Val> currNode;
    private final InvokeExpr invokeExpr;

    private CallSiteCalleeListener(
        Method caller,
        Edge callSiteEdge,
        Node<ControlFlowGraph.Edge, Val> currNode,
        InvokeExpr invokeExpr) {
      this.caller = caller;
      this.callSiteEdge = callSiteEdge;
      this.callSite = callSiteEdge.getStart();
      this.currNode = currNode;
      this.invokeExpr = invokeExpr;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + getOuterType().hashCode();
      result = prime * result + ((callSiteEdge == null) ? 0 : callSiteEdge.hashCode());
      result = prime * result + ((caller == null) ? 0 : caller.hashCode());
      result = prime * result + ((currNode == null) ? 0 : currNode.hashCode());
      return result;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null) return false;
      if (getClass() != obj.getClass()) return false;
      CallSiteCalleeListener other = (CallSiteCalleeListener) obj;
      if (!getOuterType().equals(other.getOuterType())) return false;
      if (callSiteEdge == null) {
        if (other.callSiteEdge != null) return false;
      } else if (!callSiteEdge.equals(other.callSiteEdge)) return false;
      if (caller == null) {
        if (other.caller != null) return false;
      } else if (!caller.equals(other.caller)) return false;
      if (currNode == null) {
        return other.currNode == null;
      } else return currNode.equals(other.currNode);
    }

    @Override
    public void onCalleeAdded(Statement callSite, Method callee) {
      if (callee.isStaticInitializer()) {
        return;
      }
      LOGGER.trace(
          "Call-flow of {} at callsite: {} to callee method: {} for {}",
          currNode.fact(),
          callSite,
          callee,
          this);
      for (Statement calleeSp : icfg.getStartPointsOf(callee)) {
        Collection<? extends State> res =
            computeCallFlow(
                caller, callSite, callSiteEdge, currNode, callee, new Edge(calleeSp, calleeSp));
        for (State s : res) {
          propagate(currNode, s);
        }
      }
    }

    @Override
    public void onNoCalleeFound() {
      byPassFlowAtCallSite(caller, currNode, callSite);
    }

    @Override
    public Statement getObservedCaller() {
      return callSite;
    }

    private ForwardBoomerangSolver getOuterType() {
      return ForwardBoomerangSolver.this;
    }
  }

  @Override
  public void applyCallSummary(
      ControlFlowGraph.Edge returnSiteStatement,
      Val factInCallee,
      Edge spInCallee,
      Edge lastCfgEdgeInCallee,
      Val returnedFact) {
    Statement callSite = returnSiteStatement.getStart();

    Set<Node<ControlFlowGraph.Edge, Val>> out = new LinkedHashSet<>();
    if (callSite.containsInvokeExpr()) {
      if (returnedFact.isThisLocal()) {
        if (callSite.getInvokeExpr().isInstanceInvokeExpr()) {
          out.add(new Node<>(returnSiteStatement, callSite.getInvokeExpr().getBase()));
        }
      }
      if (returnedFact.isReturnLocal()) {
        if (callSite.isAssignStmt()) {
          out.add(new Node<>(returnSiteStatement, callSite.getLeftOp()));
        }
      }
      for (int i = 0; i < callSite.getInvokeExpr().getArgs().size(); i++) {
        if (returnedFact.isParameterLocal(i)) {
          out.add(new Node<>(returnSiteStatement, callSite.getInvokeExpr().getArg(i)));
        }
      }
    }
    if (returnedFact.isStatic()) {
      out.add(
          new Node<>(
              returnSiteStatement,
              returnedFact.withNewMethod(returnSiteStatement.getStart().getMethod())));
    }
    for (Node<ControlFlowGraph.Edge, Val> xs : out) {
      addNormalCallFlow(new Node<>(returnSiteStatement, returnedFact), xs);
      addNormalFieldFlow(new Node<>(lastCfgEdgeInCallee, returnedFact), xs);
    }
  }

  public Collection<? extends State> computeCallFlow(
      Method caller,
      Statement callSite,
      Edge succOfCallSite,
      Node<ControlFlowGraph.Edge, Val> currNode,
      Method callee,
      Edge calleeStartEdge) {
    if (dataFlowScope.isExcluded(callee)) {
      byPassFlowAtCallSite(caller, currNode, callSite);
      return Collections.emptySet();
    }
    Val fact = currNode.fact();
    return flowFunctions.callFlow(callSite, fact, callee).stream()
        .map(x -> new PushNode<>(calleeStartEdge, x, succOfCallSite, PDSSystem.CALLS))
        .collect(Collectors.toSet());
  }

  public Query getQuery() {
    return query;
  }

  @Override
  public void computeSuccessor(Node<Edge, Val> node) {
    Edge curr = node.stmt();
    Val value = node.fact();
    assert !(value instanceof AllocVal);
    Method method = curr.getTarget().getMethod();
    if (method == null) return;
    if (dataFlowScope.isExcluded(method)) {
      return;
    }
    if (icfg.isExitStmt(curr.getTarget())) {
      returnFlow(method, node);
      return;
    }
    ((StaticCFG) cfg).setCurrentVal(value);
    cfg.addSuccsOfListener(
        new ForwardSolverSuccessorListener(curr, query, value, method, node, LOGGER, this));
  }

  public BoomerangOptions getOptions() {
    return options;
  }

  public void checkForFieldOverwrite(Edge curr, Val value) {
    if ((curr.getTarget().isFieldStore()
        && curr.getTarget().getFieldStore().getX().equals(value))) {
      Node<ControlFlowGraph.Edge, Val> node = new Node<>(curr, value);
      fieldAutomaton.registerListener(new OverwriteAtFieldStore(new SingleNode<>(node), curr));
    } else if ((curr.getTarget().isArrayStore()
        && curr.getTarget().getArrayBase().getX().equals(value))) {
      Node<ControlFlowGraph.Edge, Val> node = new Node<>(curr, value);
      fieldAutomaton.registerListener(new OverwriteAtArrayStore(new SingleNode<>(node), curr));
    }
  }

  protected abstract void overwriteFieldAtStatement(
      Edge fieldWriteStatementEdge, Transition<Field, INode<Node<Edge, Val>>> killedTransition);

  @Override
  public Collection<State> computeNormalFlow(Method method, Edge nextEdge, Val fact) {
    return flowFunctions.normalFlow(query, nextEdge, fact);
  }

  public void callFlow(
      Method caller,
      Node<ControlFlowGraph.Edge, Val> currNode,
      Edge callSiteEdge,
      InvokeExpr invokeExpr) {
    assert icfg.isCallStmt(callSiteEdge.getStart());
    if (dataFlowScope.isExcluded(invokeExpr.getMethod())) {
      byPassFlowAtCallSite(caller, currNode, callSiteEdge.getStart());
      return;
    }

    icfg.addCalleeListener(new CallSiteCalleeListener(caller, callSiteEdge, currNode, invokeExpr));
  }

  private void byPassFlowAtCallSite(
      Method caller, Node<ControlFlowGraph.Edge, Val> currNode, Statement callSite) {
    LOGGER.trace(
        "Bypassing call flow of {} at callsite: {} for {}", currNode.fact(), callSite, this);

    cfg.addSuccsOfListener(
        new SuccessorListener(currNode.stmt().getTarget()) {

          @Override
          public void getSuccessor(Statement returnSite) {
            for (State s :
                flowFunctions.callToReturnFlow(
                    query, new Edge(callSite, returnSite), currNode.fact())) {
              propagate(currNode, s);
            }
          }
        });
  }

  @Override
  public Collection<? extends State> computeReturnFlow(Method method, Statement curr, Val value) {
    return flowFunctions.returnFlow(method, curr, value).stream()
        .map(x -> new PopNode<>(x, PDSSystem.CALLS))
        .collect(Collectors.toSet());
  }

  @Override
  public String toString() {
    return "ForwardSolver: " + query;
  }
}
