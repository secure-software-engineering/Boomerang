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

import boomerang.scope.ControlFlowGraph;
import boomerang.scope.Statement;
import boomerang.scope.Val;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nonnull;
import sync.pds.solver.nodes.Node;
import wpds.impl.Weight;

public class DataFlowPathWeightOne implements DataFlowPathWeight {

  @Nonnull private static final DataFlowPathWeightOne one = new DataFlowPathWeightOne();

  public DataFlowPathWeightOne() {}

  public static DataFlowPathWeightOne one() {
    return one;
  }

  @Nonnull
  @Override
  public PathTrackingWeight getPath() {
    throw new IllegalStateException("MinDistanceWeight.getpath() - don't");
  }

  @Nonnull
  @Override
  public PathConditionWeight getCondition() {
    throw new IllegalStateException("MinDistanceWeight.getcondition() - don't");
  }

  @Nonnull
  @Override
  public Set<Node<ControlFlowGraph.Edge, Val>> getAllStatements() {
    throw new IllegalStateException("MinDistanceWeight.getAllStatements() - don't");
  }

  @Nonnull
  @Override
  public Map<Statement, PathConditionWeightImpl.ConditionDomain> getConditions() {
    throw new IllegalStateException("MinDistanceWeight.getAllStatements() - don't");
  }

  @Nonnull
  @Override
  public Map<Val, PathConditionWeightImpl.ConditionDomain> getEvaluationMap() {
    throw new IllegalStateException("MinDistanceWeight.getEvaluationMap() - don't");
  }

  @Nonnull
  @Override
  public Weight extendWith(@Nonnull Weight other) {
    PathTrackingWeight pathTrackingWeight =
        (PathTrackingWeight)
            PathTrackingWeightOne.one().extendWith(((DataFlowPathWeight) other).getPath());
    PathConditionWeight pathConditionWeight =
        (PathConditionWeight)
            PathConditionWeightOne.one().extendWith(((DataFlowPathWeight) other).getCondition());
    return new DataFlowPathWeightImpl(pathTrackingWeight, pathConditionWeight);
  }

  @Nonnull
  @Override
  public Weight combineWith(@Nonnull Weight other) {
    PathTrackingWeight pathTrackingWeight =
        (PathTrackingWeight)
            PathTrackingWeightOne.one().combineWith(((DataFlowPathWeightOne) other).getPath());
    PathConditionWeight pathConditionWeight =
        (PathConditionWeight)
            PathConditionWeightOne.one()
                .combineWith(((DataFlowPathWeightOne) other).getCondition());
    return new DataFlowPathWeightImpl(pathTrackingWeight, pathConditionWeight);
  }
}
