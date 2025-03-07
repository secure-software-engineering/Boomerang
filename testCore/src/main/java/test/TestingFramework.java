package test;

import boomerang.scope.DataFlowScope;
import boomerang.scope.FrameworkScope;
import boomerang.scope.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.Assert;
import test.setup.MethodWrapper;
import test.setup.SootTestSetup;
import test.setup.TestSetup;

public class TestingFramework {

  private final TestSetup testSetup;

  public TestingFramework() {
    // TODO Parameterize
    this.testSetup = new SootTestSetup();
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
    return Collections.emptyList();
  }

  protected List<String> getExcludedPackages() {
    return Collections.emptyList();
  }
}
