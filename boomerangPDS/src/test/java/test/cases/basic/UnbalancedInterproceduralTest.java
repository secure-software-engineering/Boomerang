package test.cases.basic;

import org.junit.Test;
import test.core.AbstractBoomerangTest;

public class UnbalancedInterproceduralTest extends AbstractBoomerangTest {

  private final String target = UnbalancedInterproceduralTarget.class.getName();

  @Test
  public void unbalancedCreation() {
    analyze(target, testName.getMethodName());
  }

  @Test
  public void doubleUnbalancedCreation() {
    analyze(target, testName.getMethodName());
  }
}
