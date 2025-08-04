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

import boomerang.options.IAllocationSite;
import boomerang.scope.FrameworkScope;
import boomerang.solver.BackwardBoomerangSolver;
import boomerang.solver.ForwardBoomerangSolver;

public class DefaultFlowFunctionFactory implements IFlowFunctionFactory {

  private final FlowFunctionOptions options;

  public DefaultFlowFunctionFactory() {
    this(FlowFunctionOptions.DEFAULT());
  }

  public DefaultFlowFunctionFactory(FlowFunctionOptions options) {
    this.options = options;
  }

  @Override
  public IForwardFlowFunction createForwardFlowFunction(
      FrameworkScope frameworkScope,
      ForwardBoomerangSolver<?> solver,
      IAllocationSite allocationSite) {
    return new DefaultForwardFlowFunction(options);
  }

  @Override
  public IBackwardFlowFunction createBackwardFlowFunction(
      FrameworkScope frameworkScope,
      BackwardBoomerangSolver<?> solver,
      IAllocationSite allocationSite) {
    return new DefaultBackwardFlowFunction(options);
  }
}
