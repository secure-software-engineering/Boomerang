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
package test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.InvocationInterceptor;
import org.junit.jupiter.api.extension.ReflectiveInvocationContext;
import typestate.finiteautomata.TypeStateMachineWeightFunctions;

public class IDEalTestRunnerInterceptor
    implements BeforeAllCallback, InvocationInterceptor, AfterEachCallback {

  private IDEalTestingFramework testingFramework;

  @Override
  public void beforeAll(ExtensionContext context) {
    Optional<Class<?>> testClass = context.getTestClass();
    if (testClass.isEmpty()) {
      throw new RuntimeException("Could not load test class");
    }

    TestConfig testConfig = testClass.get().getAnnotation(TestConfig.class);
    if (testConfig == null) {
      throw new RuntimeException(
          "Test class '"
              + testClass.get().getName()
              + "' is not annotated with '"
              + TestConfig.class.getSimpleName()
              + "'");
    }

    TypeStateMachineWeightFunctions stateMachine;
    try {
      stateMachine =
          (TypeStateMachineWeightFunctions)
              Class.forName(testConfig.stateMachine().getName())
                  .getDeclaredConstructor()
                  .newInstance();
    } catch (ClassNotFoundException
        | NoSuchMethodException
        | InstantiationException
        | IllegalAccessException
        | InvocationTargetException e) {
      throw new RuntimeException(
          "Could not instantiate state machine "
              + testConfig.stateMachine().getName()
              + ": "
              + e.getMessage());
    }

    Collection<String> includedClasses =
        Arrays.stream(testConfig.includedClasses())
            .map(Class::getName)
            .collect(Collectors.toList());
    Collection<String> excludedClasses =
        Arrays.stream(testConfig.excludedClasses())
            .map(Class::getName)
            .collect(Collectors.toList());
    testingFramework = new IDEalTestingFramework(stateMachine, includedClasses, excludedClasses);
  }

  @Override
  public void interceptTestMethod(
      Invocation<Void> invocation,
      ReflectiveInvocationContext<Method> invocationContext,
      ExtensionContext extensionContext) {
    String testClassName = invocationContext.getExecutable().getDeclaringClass().getName();
    String testMethodName = invocationContext.getExecutable().getName();

    TestParameters parameters =
        invocationContext.getExecutable().getAnnotation(TestParameters.class);
    if (parameters == null) {
      throw new RuntimeException(
          "Test method '"
              + testMethodName
              + "' in class '"
              + testClassName
              + "' is not annotated with '"
              + TestConfig.class.getSimpleName()
              + "'");
    }

    testingFramework.analyze(
        testClassName,
        testMethodName,
        parameters.expectedSeedCount(),
        parameters.expectedAssertionCount());

    try {
      invocation.proceed();
    } catch (Throwable ignored) {

    }
  }

  @Override
  public void afterEach(ExtensionContext context) {
    testingFramework.cleanUp();
  }
}
