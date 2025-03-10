package typestate.tests;

import org.junit.Test;
import test.IDEALTestingFramework;
import typestate.finiteautomata.TypeStateMachineWeightFunctions;
import typestate.impl.statemachines.PrintStreamStateMachine;
import typestate.targets.PrintStreamLong;

public class PrintStreamLongTest extends IDEALTestingFramework {

  private final String target = PrintStreamLong.class.getName();

  @Override
  protected TypeStateMachineWeightFunctions getStateMachine() {
    return new PrintStreamStateMachine();
  }

  @Test
  public void test1() {
    analyze(target, testName.getMethodName(), 1, 1);
  }

  @Test
  public void test() {
    analyze(target, testName.getMethodName(), 1, 1);
  }
}
