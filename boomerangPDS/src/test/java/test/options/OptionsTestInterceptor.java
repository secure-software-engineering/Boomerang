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
package test.options;

import boomerang.flowfunction.IFlowFunctionFactory;
import boomerang.options.BoomerangOptions;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.InvocationInterceptor;
import org.junit.jupiter.api.extension.ReflectiveInvocationContext;
import test.core.BoomerangTestingOptionsBuilder;

public class OptionsTestInterceptor implements InvocationInterceptor, AfterEachCallback {

  private OptionsTestingFramework testingFramework;

  @Override
  public void interceptTestMethod(
      Invocation<Void> invocation,
      ReflectiveInvocationContext<Method> invocationContext,
      ExtensionContext extensionContext) {
    String testClassName = invocationContext.getExecutable().getDeclaringClass().getName();
    String testMethodName = invocationContext.getExecutable().getName();

    testingFramework = new OptionsTestingFramework();

    TestOptions testOptions = invocationContext.getExecutable().getAnnotation(TestOptions.class);
    if (testOptions == null) {
      throw new RuntimeException(
          "Method that tests the options requires the 'TestOptions' annotation");
    }

    BoomerangOptions options = createBoomerangOptions(testOptions);
    testingFramework.analyze(
        testClassName, testMethodName, options, testOptions.expectedAllocSites());

    try {
      invocation.proceed();
    } catch (Throwable ignored) {
    }
  }

  @Override
  public void afterEach(ExtensionContext context) {
    testingFramework.cleanUp();
  }

  private BoomerangOptions createBoomerangOptions(TestOptions testOptions) {
    IFlowFunctionFactory flowFunctionFactory;
    try {
      flowFunctionFactory =
          (IFlowFunctionFactory)
              Class.forName(testOptions.flowFunctionFactory().getName())
                  .getDeclaredConstructor()
                  .newInstance();
    } catch (ClassNotFoundException
        | NoSuchMethodException
        | InstantiationException
        | IllegalAccessException
        | InvocationTargetException e) {
      throw new RuntimeException(
          "Could not instantiate flow function factory "
              + testOptions.flowFunctionFactory().getName(),
          e);
    }

    return BoomerangTestingOptionsBuilder.create()
        .withFlowFunctionFactory(flowFunctionFactory)
        .withArrayStrategy(testOptions.arrayStrategy())
        .withStaticFieldStrategy(testOptions.staticFieldStrategy())
        .enableTrackStaticFieldAtEntryPointToClinit(
            testOptions.trackStaticFieldAtEntryPointToClinit())
        .withMaxFieldDepth(testOptions.maxFieldDepth())
        .withMaxCallDepth(testOptions.maxCallDepth())
        .withAnalysisTimeout(testOptions.timeout())
        .enableFieldSensitivity(testOptions.fieldSensitivity())
        .enableContextSensitivity(testOptions.contextSensitivity())
        .enableCallSummaries(testOptions.callSummaries())
        .enableFieldSummaries(testOptions.fieldSummaries())
        .build();
  }
}
