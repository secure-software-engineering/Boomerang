package test.cases.callgraph;

import org.junit.Ignore;
import org.junit.Test;
import test.core.AbstractBoomerangTest;

public class ContextSensitivityFieldTest extends AbstractBoomerangTest {

  private final String target = ContextSensitivityFieldTarget.class.getName();

  // Method WrongSubclass.foo(Object o) is incorrectly marked as reachable.
  @Ignore
  @Test
  public void testOnlyCorrectContextInCallGraph() {
    analyze(target, testName.getMethodName());
  }
}
