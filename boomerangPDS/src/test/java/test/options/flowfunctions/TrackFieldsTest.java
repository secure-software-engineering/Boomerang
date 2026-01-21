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
package test.options.flowfunctions;

import boomerang.flowfunction.DefaultBackwardFlowFunction;
import boomerang.flowfunction.DefaultFlowFunctionFactory;
import boomerang.flowfunction.DefaultForwardFlowFunction;
import boomerang.flowfunction.FlowFunctionOptions;
import boomerang.flowfunction.IBackwardFlowFunction;
import boomerang.flowfunction.IForwardFlowFunction;
import boomerang.options.BoomerangOptions;
import boomerang.scope.FrameworkScope;
import boomerang.solver.BackwardBoomerangSolver;
import boomerang.solver.ForwardBoomerangSolver;
import boomerang.solver.Strategies;
import org.junit.jupiter.api.Test;
import test.options.OptionAssertions;
import test.options.TestOptions;

public class TrackFieldsTest {

  @Test
  @TestOptions(expectedAllocSites = {"alloc"})
  public void positiveTrackFieldsTest() {
    ClassWithField c = new ClassWithField();
    c.field = "alloc";
    OptionAssertions.queryFor(c.field);
  }

  @Test
  @TestOptions(
      expectedAllocSites = {"alloc"},
      flowFunctionFactory = ExtendedFlowFunctionFactory.class)
  public void negativeTrackFieldsTest() {
    ClassWithField c = new ClassWithField();
    c.field = "alloc";
    OptionAssertions.queryFor(c.field);
  }

  private static class ClassWithField {
    private String field;
  }

  public static class ExtendedFlowFunctionFactory extends DefaultFlowFunctionFactory {

    private final FlowFunctionOptions flowFunctionOptions =
        FlowFunctionOptions.builder().enableTrackFields(false).build();

    @Override
    public IForwardFlowFunction createForwardFlowFunction(
        FrameworkScope frameworkScope, BoomerangOptions options, ForwardBoomerangSolver<?> solver) {
      Strategies strategies = createStrategies(frameworkScope, options, solver);

      return new DefaultForwardFlowFunction(strategies, flowFunctionOptions);
    }

    @Override
    public IBackwardFlowFunction createBackwardFlowFunction(
        FrameworkScope frameworkScope,
        BoomerangOptions options,
        BackwardBoomerangSolver<?> solver) {
      Strategies strategies = createStrategies(frameworkScope, options, solver);

      return new DefaultBackwardFlowFunction(
          options.allocationSite(), strategies, flowFunctionOptions);
    }
  }
}
