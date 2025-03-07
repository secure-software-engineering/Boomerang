package test.cases.callgraph;

import boomerang.options.BoomerangOptions;
import org.junit.Ignore;
import org.junit.Test;
import test.core.AbstractBoomerangTest;

public class ContextSpecificListTypeTest extends AbstractBoomerangTest {

  private final String target = ContextSpecificListTypeTarget.class.getName();

  @Ignore
  @Test
  public void testListType() {
    analyze(target, testName.getMethodName());
  }

  @Override
  protected BoomerangOptions createBoomerangOptions() {
    return BoomerangOptions.builder()
        .enableOnTheFlyCallGraph(true)
        .enableAllowMultipleQueries(true)
        .build();
  }
}
