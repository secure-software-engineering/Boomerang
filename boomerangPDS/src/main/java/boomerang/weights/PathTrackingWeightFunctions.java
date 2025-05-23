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
package boomerang.weights;

import boomerang.scope.ControlFlowGraph.Edge;
import boomerang.scope.Val;
import boomerang.weights.PathConditionWeight.ConditionDomain;
import sync.pds.solver.WeightFunctions;
import sync.pds.solver.nodes.Node;

public class PathTrackingWeightFunctions
    implements WeightFunctions<Edge, Val, Edge, DataFlowPathWeight> {

  private final boolean trackDataFlowPath;
  private final boolean trackPathConditions;
  private final boolean implicitBooleanCondition;

  public PathTrackingWeightFunctions(
      boolean trackDataFlowPath, boolean trackPathConditions, boolean implicitBooleanCondition) {
    this.trackDataFlowPath = trackDataFlowPath;
    this.trackPathConditions = trackPathConditions;
    this.implicitBooleanCondition = implicitBooleanCondition;
  }

  @Override
  public DataFlowPathWeight push(Node<Edge, Val> curr, Node<Edge, Val> succ, Edge callSite) {
    if (trackDataFlowPath && !curr.fact().isStatic()) {
      if (callSite.getStart().uses(curr.fact())) {
        if (implicitBooleanCondition && callSite.getTarget().isAssignStmt()) {
          return new DataFlowPathWeightImpl(
              new Node<>(callSite, curr.fact()), callSite.getStart(), succ.stmt().getMethod());
        }
        return new DataFlowPathWeightImpl(new Node<>(callSite, curr.fact()));
      }
      if (implicitBooleanCondition && callSite.getStart().isAssignStmt()) {
        return new DataFlowPathWeightImpl(callSite.getStart(), succ.stmt().getMethod());
      }
    }
    return DataFlowPathWeightOne.one();
  }

  @Override
  public DataFlowPathWeight normal(Node<Edge, Val> curr, Node<Edge, Val> succ) {
    if (trackDataFlowPath
        && curr.stmt().getMethod().getControlFlowGraph().getStartPoints().contains(curr.stmt())) {
      return new DataFlowPathWeightImpl(curr);
    }
    if (trackDataFlowPath && !curr.fact().equals(succ.fact())) {
      return new DataFlowPathWeightImpl(succ);
    }
    if (trackDataFlowPath
        && succ.stmt().getTarget().isReturnStmt()
        && succ.stmt().getTarget().getReturnOp().equals(succ.fact())) {
      return new DataFlowPathWeightImpl(succ);
    }
    if (implicitBooleanCondition
        && curr.stmt().getTarget().isAssignStmt()
        && curr.stmt().getTarget().getLeftOp().getType().isBooleanType()) {
      return new DataFlowPathWeightImpl(
          curr.stmt().getTarget().getLeftOp(),
          curr.stmt().getTarget().getRightOp().toString().equals("0")
              ? ConditionDomain.FALSE
              : ConditionDomain.TRUE);
    }

    if (implicitBooleanCondition && succ.stmt().getTarget().isReturnStmt()) {
      return new DataFlowPathWeightImpl(succ.stmt().getTarget().getReturnOp());
    }

    if (trackPathConditions && curr.stmt().getTarget().isIfStmt()) {
      if (curr.stmt().getTarget().getIfStmt().getTarget().equals(succ.stmt())) {
        return new DataFlowPathWeightImpl(curr.stmt().getTarget(), true);
      }
      return new DataFlowPathWeightImpl(curr.stmt().getTarget(), false);
    }
    return DataFlowPathWeightOne.one();
  }

  @Override
  public DataFlowPathWeight pop(Node<Edge, Val> curr) {
    return DataFlowPathWeightOne.one();
  }

  @Override
  public DataFlowPathWeight getOne() {
    return DataFlowPathWeightOne.one();
  }
}
