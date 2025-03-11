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

import boomerang.ForwardQuery;
import boomerang.WeightedBoomerang;
import boomerang.options.BoomerangOptions;
import boomerang.scope.ControlFlowGraph.Edge;
import boomerang.scope.Field;
import boomerang.scope.FrameworkScope;
import boomerang.scope.Val;
import sync.pds.solver.OneWeightFunctions;
import sync.pds.solver.WeightFunctions;

import javax.annotation.Nonnull;

public abstract class PathTrackingBoomerang extends WeightedBoomerang<DataFlowPathWeight> {

  private OneWeightFunctions<Edge, Val, Field, DataFlowPathWeight> fieldWeights;
  private PathTrackingWeightFunctions callWeights;

  public PathTrackingBoomerang(@Nonnull FrameworkScope frameworkScope) {
    super(frameworkScope);
  }

  public PathTrackingBoomerang(@Nonnull FrameworkScope frameworkScope, BoomerangOptions options) {
    super(frameworkScope, options);
  }

  @Override
  protected WeightFunctions<Edge, Val, Field, DataFlowPathWeight> getForwardFieldWeights() {
    return getOrCreateFieldWeights();
  }

  @Override
  protected WeightFunctions<Edge, Val, Field, DataFlowPathWeight> getBackwardFieldWeights() {
    return getOrCreateFieldWeights();
  }

  @Override
  protected WeightFunctions<Edge, Val, Edge, DataFlowPathWeight> getBackwardCallWeights() {
    return getOrCreateCallWeights();
  }

  @Override
  protected WeightFunctions<Edge, Val, Edge, DataFlowPathWeight> getForwardCallWeights(
      ForwardQuery sourceQuery) {
    return getOrCreateCallWeights();
  }

  @Nonnull
  private WeightFunctions<Edge, Val, Field, DataFlowPathWeight> getOrCreateFieldWeights() {
    if (fieldWeights == null) {
      fieldWeights = new OneWeightFunctions<>(DataFlowPathWeightOne.one());
    }
    return fieldWeights;
  }

  @Nonnull
  private WeightFunctions<Edge, Val, Edge, DataFlowPathWeight> getOrCreateCallWeights() {
    if (callWeights == null) {
      callWeights =
          new PathTrackingWeightFunctions(
              options.trackDataFlowPath(),
              options.trackPathConditions(),
              options.trackImplicitFlows());
    }
    return callWeights;
  }
}
