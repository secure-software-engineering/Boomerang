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
package test.core;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.InvocationInterceptor;
import org.junit.jupiter.api.extension.ReflectiveInvocationContext;
import test.TestingFramework;

public class BoomerangTestRunnerInterceptor
    implements BeforeAllCallback, InvocationInterceptor, AfterEachCallback {

  private Collection<String> includedClasses = Collections.emptyList();
  private Collection<String> excludedClasses = Collections.emptyList();
  private BoomerangTestingFramework testingFramework;

  @Override
  public void beforeAll(ExtensionContext context) {
    Optional<Class<?>> testClass = context.getTestClass();
    if (testClass.isEmpty()) {
      throw new RuntimeException("Could not load test class");
    }

    TestConfig testConfig = testClass.get().getAnnotation(TestConfig.class);
    if (testConfig != null) {
      includedClasses = Arrays.stream(testConfig.includedClasses()).collect(Collectors.toList());
      excludedClasses = Arrays.stream(testConfig.excludedClasses()).collect(Collectors.toList());
    }
  }

  @Override
  public void interceptTestMethod(
      Invocation<Void> invocation,
      ReflectiveInvocationContext<Method> invocationContext,
      ExtensionContext extensionContext) {
    String testClassName = invocationContext.getExecutable().getDeclaringClass().getName();
    String testMethodName = invocationContext.getExecutable().getName();

    testingFramework = new BoomerangTestingFramework(includedClasses, excludedClasses);

    TestParameters parameters =
        invocationContext.getExecutable().getAnnotation(TestParameters.class);
    if (parameters == null) {
      testingFramework.analyze(testClassName, testMethodName);
    } else {
      for (TestingFramework.Framework framework : parameters.skipFramework()) {
        Assumptions.assumeFalse(
            framework == testingFramework.getFramework(),
            "Test is configured to skip with " + framework);
      }
      testingFramework.analyze(testClassName, testMethodName, parameters.ignoreAllocSites());
    }

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
