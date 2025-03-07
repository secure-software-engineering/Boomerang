package test.cases.callgraph;

import org.junit.Ignore;
import org.junit.Test;
import test.core.AbstractBoomerangTest;

public class ContextSensitivityTest extends AbstractBoomerangTest {

  private final String target = ContextSensitivityTarget.class.getName();

  @Ignore
  @Test
  public void testOnlyCorrectContextInCallGraph() {
    analyze(target, testName.getMethodName());
  }
}
