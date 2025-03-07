package typestate.tests;

import java.util.List;
import org.junit.Test;
import test.IDEALTestingFramework;
import typestate.finiteautomata.TypeStateMachineWeightFunctions;
import typestate.impl.statemachines.HasNextStateMachine;
import typestate.targets.IteratorHasNext;

public class IteratorHasNextTest extends IDEALTestingFramework {

  private final String target = IteratorHasNext.class.getName();

  @Override
  protected List<String> getIncludedPackages() {
    return List.of("java.util.Iterator");
  }

  @Override
  protected TypeStateMachineWeightFunctions getStateMachine() {
    return new HasNextStateMachine();
  }

  @Test
  public void test1() {
    analyze(target, testName.getMethodName(), 1, 2);
  }

  @Test
  public void test2() {
    analyze(target, testName.getMethodName(), 1, 1);
  }

  @Test
  public void test3() {
    analyze(target, testName.getMethodName(), 1, 1);
  }

  @Test
  public void test4() {
    analyze(target, testName.getMethodName(), 1, 1);
  }

  @Test
  public void chartTest() {
    analyze(target, testName.getMethodName(), 4, 2);
  }
}
