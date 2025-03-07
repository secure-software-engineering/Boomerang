package typestate.tests;

import java.util.List;
import org.junit.Ignore;
import org.junit.Test;
import test.IDEALTestingFramework;
import typestate.finiteautomata.TypeStateMachineWeightFunctions;
import typestate.impl.statemachines.VectorStateMachine;
import typestate.targets.StackLong;

public class StackLongTest extends IDEALTestingFramework {

  private final String target = StackLong.class.getName();

  @Override
  protected TypeStateMachineWeightFunctions getStateMachine() {
    return new VectorStateMachine();
  }

  @Override
  protected List<String> getIncludedPackages() {
    return List.of("java.util.Stack", "java.util.Vector");
  }

  @Test
  public void test1() {
    analyze(target, testName.getMethodName(), 2, 1);
  }

  @Test
  public void test4simple() {
    analyze(target, testName.getMethodName(), 2, 1);
  }

  @Test
  public void test2() {
    analyze(target, testName.getMethodName(), 1, 1);
  }

  @Test
  public void test6() {
    analyze(target, testName.getMethodName(), 1, 1);
  }

  @Test
  public void test3() {
    analyze(target, testName.getMethodName(), 2, 1);
  }

  @Test
  public void test5() {
    analyze(target, testName.getMethodName(), 1, 1);
  }

  @Test
  public void test4() {
    analyze(target, testName.getMethodName(), 2, 2);
  }

  @Ignore("Broken since refactoring scope")
  @Test
  public void testInNewObject() {
    analyze(target, testName.getMethodName(), 2, 1);
  }
}
