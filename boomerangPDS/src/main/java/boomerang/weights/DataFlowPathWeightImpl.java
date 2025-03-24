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
package boomerang.weights;

import boomerang.scope.ControlFlowGraph.Edge;
import boomerang.scope.Method;
import boomerang.scope.Statement;
import boomerang.scope.Val;
import boomerang.weights.PathConditionWeight.ConditionDomain;
import com.google.common.base.Objects;
import java.util.Map;
import java.util.Set;
import org.jspecify.annotations.NonNull;
import sync.pds.solver.nodes.Node;
import wpds.impl.Weight;

public class DataFlowPathWeightImpl implements DataFlowPathWeight {

    private final PathTrackingWeight path;
  private final PathConditionWeight condition;



  public DataFlowPathWeightImpl(Node<Edge, Val> path) {
    this.path = new PathTrackingWeightImpl(path);
    this.condition = PathConditionWeightOne.one();
  }

  public DataFlowPathWeightImpl(Node<Edge, Val> path, Statement callSite, Method callee) {
    this.path = new PathTrackingWeightImpl(path);
    this.condition = new PathConditionWeightImpl(callSite, callee);
  }

  public DataFlowPathWeightImpl(Statement callSite, Method callee) {
    this.path = PathTrackingWeightOne.one();
    this.condition = new PathConditionWeightImpl(callSite, callee);
  }

  public DataFlowPathWeightImpl(Statement ifStatement, Boolean condition) {
    this.path = PathTrackingWeightOne.one();
    this.condition = new PathConditionWeightImpl(ifStatement, condition);
  }

  DataFlowPathWeightImpl(PathTrackingWeight path, PathConditionWeight condition) {
    this.path = path;
    this.condition = condition;
  }

  public DataFlowPathWeightImpl(Val leftOp, ConditionDomain conditionVal) {
    this.path = PathTrackingWeightOne.one();
    this.condition = new PathConditionWeightImpl(leftOp, conditionVal);
  }

  public DataFlowPathWeightImpl(Val returnVal) {
    this.path = PathTrackingWeightOne.one();
    this.condition = new PathConditionWeightImpl(returnVal);
  }

  @NonNull
  @Override
  public PathTrackingWeight getPath() {
    return path;
  }

  @NonNull
  @Override
  public PathConditionWeight getCondition() {
    return condition;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    DataFlowPathWeightImpl that = (DataFlowPathWeightImpl) o;
    return Objects.equal(path, that.path) && Objects.equal(condition, that.condition);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(path, condition);
  }

  @NonNull
  @Override
  public Set<Node<Edge, Val>> getAllStatements() {
    return path.getShortestPathWitness();
  }

  @NonNull
  @Override
  public Map<Statement, ConditionDomain> getConditions() {
    return condition.getConditions();
  }

  @NonNull
  @Override
  public Map<Val, ConditionDomain> getEvaluationMap() {
    return condition.getEvaluationMap();
  }

  @Override
  public String toString() {
    return /*"PATH" + path +*/ " COND: " + condition;
  }

  @NonNull
  @Override
  public Weight extendWith(@NonNull Weight other) {
    return new DataFlowPathWeightImpl(
        (PathTrackingWeightImpl) path.extendWith(((DataFlowPathWeightImpl) other).path),
        (PathConditionWeight) condition.extendWith(((DataFlowPathWeightImpl) other).condition));
  }

  @NonNull
  @Override
  public Weight combineWith(@NonNull Weight other) {
    return new DataFlowPathWeightImpl(
        (PathTrackingWeightImpl) path.combineWith(((DataFlowPathWeightImpl) other).path),
        (PathConditionWeight) condition.combineWith(((DataFlowPathWeightImpl) other).condition));
  }
}
