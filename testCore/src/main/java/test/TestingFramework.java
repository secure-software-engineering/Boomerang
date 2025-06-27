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

import boomerang.scope.DataFlowScope;
import boomerang.scope.FrameworkScope;
import boomerang.scope.Method;
import boomerang.utils.MethodWrapper;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Assertions;
import test.setup.OpalTestSetup;
import test.setup.SootTestSetup;
import test.setup.SootUpTestSetup;
import test.setup.TestSetup;

public class TestingFramework {

  private static final String SOOT = "soot";
  private static final String SOOT_UP = "sootup";
  private static final String OPAL = "opal";

  private final TestSetup testSetup;
  private final Collection<String> includedClasses;
  private final Collection<String> excludedClasses;

  public enum Framework {
    SOOT,
    SOOT_UP,
    OPAL
  }

  private final Framework framework;

  public TestingFramework() {
    this(Collections.emptySet(), Collections.emptySet());
  }

  public TestingFramework(Collection<String> includedClasses, Collection<String> excludedClasses) {
    this.testSetup = createTestSetup();
    this.includedClasses = includedClasses;
    this.excludedClasses = excludedClasses;

    if (testSetup instanceof SootTestSetup) {
      this.framework = Framework.SOOT;
    } else if (testSetup instanceof SootUpTestSetup) {
      this.framework = Framework.SOOT_UP;
    } else if (testSetup instanceof OpalTestSetup) {
      this.framework = Framework.OPAL;
    } else {
      throw new RuntimeException("No valid framework setup: " + testSetup.getClass().getName());
    }
  }

  private TestSetup createTestSetup() {
    String framework = System.getProperty("testSetup");
    if (framework == null) {
      // This can be changed when executing tests locally
      return new SootUpTestSetup();
    }

    switch (framework.toLowerCase()) {
      case SOOT:
        return new SootTestSetup();
      case SOOT_UP:
        return new SootUpTestSetup();
      case OPAL:
        return new OpalTestSetup();
      default:
        throw new IllegalArgumentException(
            "Cannot create test setup for framework "
                + framework
                + ". Available options are {Soot, SootUp, Opal}");
    }
  }

  public Framework getFramework() {
    return framework;
  }

  public FrameworkScope getFrameworkScope(MethodWrapper methodWrapper) {
    return getFrameworkScope(methodWrapper, DataFlowScope.EXCLUDE_PHANTOM_CLASSES);
  }

  public FrameworkScope getFrameworkScope(
      MethodWrapper methodWrapper, DataFlowScope dataFlowScope) {
    String classPath = buildClassPath();
    testSetup.initialize(
        classPath, methodWrapper, List.copyOf(includedClasses), List.copyOf(excludedClasses));

    return testSetup.createFrameworkScope(dataFlowScope);
  }

  public Method getTestMethod() {
    Method testMethod = testSetup.getTestMethod();

    if (testMethod == null) {
      throw new IllegalStateException(
          "Test method not initialized. Call 'getFrameworkScope' first");
    }

    return testMethod;
  }

  public void assertResults(Collection<Assertion> assertions) {
    assertResults(assertions, true);
  }

  public void assertResults(Collection<Assertion> assertions, boolean failOnImprecise) {
    Collection<Assertion> unsound = new HashSet<>();
    Collection<Assertion> imprecise = new HashSet<>();

    for (Assertion r : assertions) {
      if (r.isUnsound()) {
        unsound.add(r);
      }
    }

    for (Assertion r : assertions) {
      if (r.isImprecise()) {
        imprecise.add(r);
      }
    }

    if (!unsound.isEmpty()) {
      Assertions.fail(
          "Unsound results:\n- "
              + unsound.stream()
                  .map(Assertion::getAssertedMessage)
                  .collect(Collectors.joining("\n- ")));
    }

    if (!imprecise.isEmpty() && failOnImprecise) {
      Assertions.fail(
          "Imprecise results:\n- "
              + imprecise.stream()
                  .map(Assertion::getAssertedMessage)
                  .collect(Collectors.joining("\n- ")));
    }
  }

  protected String buildClassPath() {
    String userDir = System.getProperty("user.dir");
    String javaHome = System.getProperty("java.home");
    if (javaHome == null || javaHome.isEmpty()) {
      throw new RuntimeException("Could not get property java.home!");
    }

    return userDir + "/target/test-classes";
  }

  public void cleanUp() {
    if (testSetup != null) {
      testSetup.cleanUp();
    }
  }
}
