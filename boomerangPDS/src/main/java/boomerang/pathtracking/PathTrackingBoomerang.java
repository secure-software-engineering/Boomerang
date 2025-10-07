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
package boomerang.pathtracking;

import boomerang.ForwardQuery;
import boomerang.WeightedBoomerang;
import boomerang.scope.ControlFlowGraph.Edge;
import boomerang.scope.Field;
import boomerang.scope.FrameworkScope;
import boomerang.scope.Val;
import org.jspecify.annotations.NonNull;
import sync.pds.solver.OneWeightFunctions;
import sync.pds.solver.WeightFunctions;

/**
 * TODO This needs a complete revisit. Currently, it is not clear what this class is doing and
 * whether it works with the refactored scopes in 3.0.0+
 */
public abstract class PathTrackingBoomerang extends WeightedBoomerang<DataFlowPathWeight> {

  private OneWeightFunctions<Edge, Val, Field, DataFlowPathWeight> fieldWeights;
  private PathTrackingWeightFunctions callWeights;
  private final PathTrackingBoomerangOptions options;

  public PathTrackingBoomerang(@NonNull FrameworkScope frameworkScope) {
    super(frameworkScope, PathTrackingBoomerangOptions.DEFAULT());

    this.options = PathTrackingBoomerangOptions.DEFAULT();
  }

  public PathTrackingBoomerang(
      @NonNull FrameworkScope frameworkScope, PathTrackingBoomerangOptions options) {
    super(frameworkScope, options);

    this.options = options;
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

  @NonNull
  private WeightFunctions<Edge, Val, Field, DataFlowPathWeight> getOrCreateFieldWeights() {
    if (fieldWeights == null) {
      fieldWeights = new OneWeightFunctions<>(DataFlowPathWeightOne.one());
    }
    return fieldWeights;
  }

  @NonNull
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
