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
import org.junit.jupiter.api.extension.ExtendWith;
import test.options.OptionAssertions;
import test.options.OptionsTestInterceptor;
import test.options.TestOptions;

@ExtendWith(OptionsTestInterceptor.class)
public class IncludeInnerClassFieldsTest {

  @Test
  @TestOptions(expectedAllocSites = {"alloc"})
  public void positiveIncludeInnerClassFieldsTest() {
    ClassWithInnerClass.InnerClass innerClass = new ClassWithInnerClass.InnerClass();
    innerClass.innerField = "alloc";
    OptionAssertions.queryFor(innerClass.innerField);
  }

  @Test
  @TestOptions(
      expectedAllocSites = {},
      flowFunctionFactory = ExtendedFlowFunctionFactory.class)
  public void negativeIncludeInnerClassFieldsTest() {
    ClassWithInnerClass.InnerClass innerClass = new ClassWithInnerClass.InnerClass();
    innerClass.innerField = "alloc";
    OptionAssertions.queryFor(innerClass.innerField);
  }

  private static class ClassWithInnerClass {

    private static class InnerClass {
      private String innerField;
    }
  }

  public static class ExtendedFlowFunctionFactory extends DefaultFlowFunctionFactory {

    private final FlowFunctionOptions flowFunctionOptions =
        FlowFunctionOptions.builder().enableIncludeInnerClassFields(false).build();

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
