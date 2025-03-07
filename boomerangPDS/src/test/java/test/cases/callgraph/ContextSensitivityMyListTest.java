package test.cases.callgraph;

import org.junit.Test;
import test.core.AbstractBoomerangTest;

public class ContextSensitivityMyListTest extends AbstractBoomerangTest {

  private final String target = ContextSensitivityMyListTarget.class.getName();

  @Test
  public void testOnlyCorrectContextInCallGraph() {
    analyze(target, testName.getMethodName());
  }
}
