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
package boomerang;

import boomerang.options.BoomerangOptions;
import boomerang.scope.ControlFlowGraph.Edge;
import boomerang.scope.Field;
import boomerang.scope.FrameworkScope;
import boomerang.scope.Val;
import sync.pds.solver.OneWeightFunctions;
import sync.pds.solver.WeightFunctions;
import wpds.impl.NoWeight;

public class Boomerang extends WeightedBoomerang<NoWeight> {

  private OneWeightFunctions<Edge, Val, Field, NoWeight> fieldWeights;
  private OneWeightFunctions<Edge, Val, Edge, NoWeight> callWeights;

  public Boomerang(FrameworkScope frameworkScope) {
    super(frameworkScope);
  }

  public Boomerang(FrameworkScope frameworkScope, BoomerangOptions options) {
    super(frameworkScope, options);
  }

  @Override
  protected WeightFunctions<Edge, Val, Field, NoWeight> getForwardFieldWeights() {
    return getOrCreateFieldWeights();
  }

  @Override
  protected WeightFunctions<Edge, Val, Field, NoWeight> getBackwardFieldWeights() {
    return getOrCreateFieldWeights();
  }

  @Override
  protected WeightFunctions<Edge, Val, Edge, NoWeight> getBackwardCallWeights() {
    return getOrCreateCallWeights();
  }

  @Override
  protected WeightFunctions<Edge, Val, Edge, NoWeight> getForwardCallWeights(
      ForwardQuery sourceQuery) {
    return getOrCreateCallWeights();
  }

  private WeightFunctions<Edge, Val, Field, NoWeight> getOrCreateFieldWeights() {
    if (fieldWeights == null) {
      fieldWeights = new OneWeightFunctions<>(NoWeight.getInstance());
    }
    return fieldWeights;
  }

  private WeightFunctions<Edge, Val, Edge, NoWeight> getOrCreateCallWeights() {
    if (callWeights == null) {
      callWeights = new OneWeightFunctions<>(NoWeight.getInstance());
    }
    return callWeights;
  }
}
