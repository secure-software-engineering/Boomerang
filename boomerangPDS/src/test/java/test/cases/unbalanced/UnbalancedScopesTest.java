package test.cases.unbalanced;

import org.junit.Test;
import test.core.AbstractBoomerangTest;

public class UnbalancedScopesTest extends AbstractBoomerangTest {

  private final String target = UnbalancedScopesTarget.class.getName();

  @Test
  public void closingContext() {
    analyze(target, testName.getMethodName());
  }

  @Test
  public void openingContext() {
    analyze(target, testName.getMethodName());
  }

  @Test
  public void doubleClosingContext() {
    analyze(target, testName.getMethodName());
  }

  @Test
  public void branchedReturn() {
    analyze(target, testName.getMethodName());
  }

  @Test
  public void summaryReuse() {
    analyze(target, testName.getMethodName());
  }
}
