/**
 * ***************************************************************************** Copyright (c) 2018
 * Fraunhofer IEM, Paderborn, Germany. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * <p>SPDX-License-Identifier: EPL-2.0
 *
 * <p>Contributors: Johannes Spaeth - initial API and implementation
 * *****************************************************************************
 */
package boomerang;

import boomerang.options.BoomerangOptions;
import boomerang.scope.ControlFlowGraph.Edge;
import boomerang.scope.Field;
import boomerang.scope.FrameworkScope;
import boomerang.scope.Val;
import sync.pds.solver.OneWeightFunctions;
import sync.pds.solver.WeightFunctions;
import wpds.impl.Weight;

public class Boomerang extends WeightedBoomerang<Weight.NoWeight> {

  private OneWeightFunctions<Edge, Val, Field, Weight.NoWeight> fieldWeights;
  private OneWeightFunctions<Edge, Val, Edge, Weight.NoWeight> callWeights;

  public Boomerang(FrameworkScope frameworkScope) {
    super(frameworkScope);
  }

  public Boomerang(FrameworkScope frameworkScope, BoomerangOptions options) {
    super(frameworkScope, options);
  }

  @Override
  protected WeightFunctions<Edge, Val, Field, Weight.NoWeight> getForwardFieldWeights() {
    return getOrCreateFieldWeights();
  }

  @Override
  protected WeightFunctions<Edge, Val, Field, Weight.NoWeight> getBackwardFieldWeights() {
    return getOrCreateFieldWeights();
  }

  @Override
  protected WeightFunctions<Edge, Val, Edge, Weight.NoWeight> getBackwardCallWeights() {
    return getOrCreateCallWeights();
  }

  @Override
  protected WeightFunctions<Edge, Val, Edge, Weight.NoWeight> getForwardCallWeights(
      ForwardQuery sourceQuery) {
    return getOrCreateCallWeights();
  }

  private WeightFunctions<Edge, Val, Field, Weight.NoWeight> getOrCreateFieldWeights() {
    if (fieldWeights == null) {
      fieldWeights = new OneWeightFunctions<>(Weight.NO_WEIGHT_ONE);
    }
    return fieldWeights;
  }

  private WeightFunctions<Edge, Val, Edge, Weight.NoWeight> getOrCreateCallWeights() {
    if (callWeights == null) {
      callWeights = new OneWeightFunctions<>(Weight.NO_WEIGHT_ONE);
    }
    return callWeights;
  }
}
