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
package test.options.boomerang;

import boomerang.flowfunction.DefaultBackwardFlowFunction;
import boomerang.flowfunction.DefaultFlowFunctionFactory;
import boomerang.flowfunction.IBackwardFlowFunction;
import boomerang.options.BoomerangOptions;
import boomerang.scope.FrameworkScope;
import boomerang.solver.BackwardBoomerangSolver;
import boomerang.solver.Strategies;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import test.options.OptionAssertions;
import test.options.OptionsTestInterceptor;
import test.options.TestOptions;

@ExtendWith(OptionsTestInterceptor.class)
public class TimeoutTest {

  @Test
  @TestOptions(expectedAllocSites = "timeout", flowFunctionFactory = TimeoutFlowFunctions.class)
  public void noTimeoutTest() {
    String s = "timeout";
    OptionAssertions.queryForString(s);
  }

  @Test
  @TestOptions(
      expectedAllocSites = "timeout",
      flowFunctionFactory = TimeoutFlowFunctions.class,
      timeout = 10000)
  public void positiveTimeoutTest() {
    String s = "timeout";
    OptionAssertions.queryForString(s);
  }

  @Test
  @TestOptions(
      expectedAllocSites = {},
      flowFunctionFactory = TimeoutFlowFunctions.class,
      timeout = 1000)
  public void negativeTimeoutTest() {
    String s = "timeout";
    OptionAssertions.queryForString(s);
  }

  /**
   * Flow function that adds a timeout of 2 seconds to each backward step. This way, we can delay
   * Boomerang's execution, leading to timeouts
   */
  public static class TimeoutFlowFunctions extends DefaultFlowFunctionFactory {

    @Override
    public IBackwardFlowFunction createBackwardFlowFunction(
        FrameworkScope frameworkScope,
        BoomerangOptions options,
        BackwardBoomerangSolver<?> solver) {
      Strategies strategies = createStrategies(frameworkScope, options, solver);

      try {
        Thread.sleep(2000);
      } catch (InterruptedException ignored) {
      }

      return new DefaultBackwardFlowFunction(options.allocationSite(), strategies);
    }
  }
}
