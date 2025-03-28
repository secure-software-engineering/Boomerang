/**
 * ***************************************************************************** 
 * Copyright (c) 2025 Fraunhofer IEM, Paderborn, Germany. This program and the
 * accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at http://www.eclipse.org/legal/epl-2.0.
 *
 * <p>SPDX-License-Identifier: EPL-2.0
 *
 * <p>Contributors: Johannes Spaeth - initial API and implementation
 * *****************************************************************************
 */
package test;

import boomerang.scope.DataFlowScope;
import boomerang.scope.FrameworkScope;
import boomerang.scope.Method;

import java.util.*;
import java.util.stream.Collectors;
import org.junit.Assert;
import test.setup.MethodWrapper;
import test.setup.SootTestSetup;
import test.setup.SootUpTestSetup;
import test.setup.TestSetup;

public class TestingFramework {

  private final TestSetup testSetup;

  public TestingFramework() {

    String framework = System.getProperty("framework");
    switch(framework){
      case "soot":
        this.testSetup = new SootTestSetup();
        break;
      case "sootup":
      default:
        this.testSetup = new SootUpTestSetup();
    }
  }

  public FrameworkScope getFrameworkScope(MethodWrapper methodWrapper) {
    return getFrameworkScope(methodWrapper, DataFlowScope.EXCLUDE_PHANTOM_CLASSES);
  }

  public FrameworkScope getFrameworkScope(
      MethodWrapper methodWrapper, DataFlowScope dataFlowScope) {
    String classPath = buildClassPath();
    testSetup.initialize(classPath, methodWrapper, getIncludedPackages(), getExcludedPackages());

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
      Assert.fail(
          "Unsound results:\n- "
              + unsound.stream()
                  .map(Assertion::getAssertedMessage)
                  .collect(Collectors.joining("\n- ")));
    }

    if (!imprecise.isEmpty() && failOnImprecise) {
      Assert.fail(
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

  protected List<String> getIncludedPackages() {
    return new ArrayList<>();
  }

  protected List<String> getExcludedPackages() {
    return new ArrayList<>();
  }
}
