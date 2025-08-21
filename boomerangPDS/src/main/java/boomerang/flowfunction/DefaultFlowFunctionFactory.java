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
package boomerang.flowfunction;

import boomerang.options.BoomerangOptions;
import boomerang.scope.FrameworkScope;
import boomerang.solver.AbstractBoomerangSolver;
import boomerang.solver.BackwardBoomerangSolver;
import boomerang.solver.ForwardBoomerangSolver;
import boomerang.solver.Strategies;

public class DefaultFlowFunctionFactory implements IFlowFunctionFactory {

  private final FlowFunctionOptions flowFunctionOptions;

  public DefaultFlowFunctionFactory() {
    this(FlowFunctionOptions.DEFAULT());
  }

  public DefaultFlowFunctionFactory(FlowFunctionOptions flowFunctionOptions) {
    this.flowFunctionOptions = flowFunctionOptions;
  }

  @Override
  public IForwardFlowFunction createForwardFlowFunction(
      FrameworkScope frameworkScope, BoomerangOptions options, ForwardBoomerangSolver<?> solver) {
    Strategies strategies = createStrategies(frameworkScope, options, solver);

    return new DefaultForwardFlowFunction(strategies, flowFunctionOptions);
  }

  @Override
  public IBackwardFlowFunction createBackwardFlowFunction(
      FrameworkScope frameworkScope, BoomerangOptions options, BackwardBoomerangSolver<?> solver) {
    Strategies strategies = createStrategies(frameworkScope, options, solver);

    return new DefaultBackwardFlowFunction(
        options.allocationSite(), strategies, flowFunctionOptions);
  }

  protected Strategies createStrategies(
      FrameworkScope frameworkScope, BoomerangOptions options, AbstractBoomerangSolver<?> solver) {
    return new Strategies(
        options.getStaticFieldStrategy(),
        options.getArrayStrategy(),
        solver,
        frameworkScope.getCallGraph().getFieldLoadStatements(),
        frameworkScope.getCallGraph().getFieldStoreStatements());
  }
}
