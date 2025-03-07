package typestate.tests;

import org.junit.Test;
import test.IDEALTestingFramework;
import typestate.finiteautomata.TypeStateMachineWeightFunctions;
import typestate.impl.statemachines.FileMustBeClosedStateMachine;
import typestate.targets.FileMustBeClosedInterface;

public class FileMustBeClosedInterfaceTest extends IDEALTestingFramework {

  private final String target = FileMustBeClosedInterface.class.getName();

  @Override
  public TypeStateMachineWeightFunctions getStateMachine() {
    return new FileMustBeClosedStateMachine();
  }

  @Test
  public void mainTest() {
    analyze(target, testName.getMethodName(), 2, 1);
  }

  @Test
  public void otherTest() {
    analyze(target, testName.getMethodName(), 4, 1);
  }
}
