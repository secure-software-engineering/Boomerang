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
package boomerang.controlflowgraph;

import boomerang.ForwardQuery;
import boomerang.scope.ControlFlowGraph;
import boomerang.scope.Method;
import boomerang.scope.Statement;
import boomerang.scope.Val;
import boomerang.solver.ForwardBoomerangSolver;
import java.util.Collection;
import sync.pds.solver.nodes.Node;
import wpds.interfaces.State;

/**
 * To replace the anonymous impl in ForwardSolver, so that we can access the Edge field of the outer
 * method
 */
public class ForwardSolverSuccessorListener extends SuccessorListener {

  private final ForwardQuery query;
  private final ControlFlowGraph.Edge curr;
  private final Val value;
  private final Method method;
  private final Node<ControlFlowGraph.Edge, Val> node;
  private final org.slf4j.Logger
      LOGGER; // doesn't look good but this class also shouldn't exist alone
  private final ForwardBoomerangSolver<?> owner;

  public ForwardSolverSuccessorListener(
      ControlFlowGraph.Edge curr,
      ForwardQuery query,
      Val value,
      Method method,
      Node<ControlFlowGraph.Edge, Val> node,
      org.slf4j.Logger LOGGER,
      ForwardBoomerangSolver<?> owner) {
    super(curr.getTarget(), value);
    this.query = query;
    this.curr = curr;
    this.value = value;
    this.method = method;
    this.node = node;
    this.LOGGER = LOGGER;
    this.owner = owner;
  }

  public ControlFlowGraph.Edge getEdge() {
    return curr;
  }

  @Override
  public void getSuccessor(Statement succ) {
    if (query.getType().isNullType()
        && curr.getStart().isIfStmt()
        && curr.getStart().killAtIfStmt(value, succ)) {
      return;
    }

    if (!method.getLocals().contains(value) && !value.isStatic()) {
      return;
    }
    if (curr.getTarget().containsInvokeExpr()
        && (curr.getTarget().isParameter(value) || value.isStatic())) {
      owner.callFlow(
          method,
          node,
          new ControlFlowGraph.Edge(curr.getTarget(), succ),
          curr.getTarget().getInvokeExpr());
    } else {
      owner.checkForFieldOverwrite(curr, value);
      Collection<State> out =
          owner.computeNormalFlow(
              method, curr, new ControlFlowGraph.Edge(curr.getTarget(), succ), value);
      for (State s : out) {
        LOGGER.trace("{}: {} -> {}", s, node, owner.getQuery());
        owner.propagate(node, s);
      }
    }
  }
}
