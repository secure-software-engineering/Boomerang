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
package typestate;

import boomerang.flowfunction.DefaultFlowFunctionFactory;
import boomerang.flowfunction.IBackwardFlowFunction;
import boomerang.flowfunction.IForwardFlowFunction;
import boomerang.options.BoomerangOptions;
import boomerang.scope.FrameworkScope;
import boomerang.solver.BackwardBoomerangSolver;
import boomerang.solver.ForwardBoomerangSolver;
import boomerang.solver.Strategies;
import boomerang.utils.MethodWrapper;
import java.util.Collection;

/**
 * Flow function factory that extends the {@link DefaultFlowFunctionFactory} by adding features to
 * deal with chained methods when applying the call-to-return flow. Intermediate representations
 * transform chained method calls into multiple statements and introduce new locals for each
 * intermediate call. Although the chained calls return the original objects (i.e. an alias), the
 * default flow function do not find the aliases. The extension in this flow function factory adds
 * the functionality to collect corresponding aliases, too.
 *
 * <p>For example, a program
 *
 * <pre>{@code
 * l.chain().chain();
 * }</pre>
 *
 * is transformed into the intermediate representation
 *
 * <pre>{@code
 * $s0 = l.chain();
 * $s1 = $s0.chain();
 * }</pre>
 *
 * The extended flow functions make sure to collect $s0 and $s1 as alias s.t. the analysis can
 * collect the second call to chain().
 */
public class ChainingFlowFunctionFactory extends DefaultFlowFunctionFactory {

  private final Collection<MethodWrapper> methodChains;

  public ChainingFlowFunctionFactory(Collection<MethodWrapper> methodChains) {
    this.methodChains = methodChains;
  }

  @Override
  public IForwardFlowFunction createForwardFlowFunction(
      FrameworkScope frameworkScope, BoomerangOptions options, ForwardBoomerangSolver<?> solver) {
    Strategies strategies = createStrategies(frameworkScope, options, solver);

    return new ChainingForwardFlowFunction(strategies, methodChains);
  }

  @Override
  public IBackwardFlowFunction createBackwardFlowFunction(
      FrameworkScope frameworkScope, BoomerangOptions options, BackwardBoomerangSolver<?> solver) {
    Strategies strategies = createStrategies(frameworkScope, options, solver);

    return new ChainingBackwardFlowFunction(options.allocationSite(), strategies, methodChains);
  }
}
