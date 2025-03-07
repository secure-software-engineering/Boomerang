package typestate.tests;

import org.junit.Test;
import test.IDEALTestingFramework;
import typestate.finiteautomata.TypeStateMachineWeightFunctions;
import typestate.impl.statemachines.FileMustBeClosedStateMachine;
import typestate.targets.FluentInterface;

public class FluentInterfaceTest extends IDEALTestingFramework {

  private final String target = FluentInterface.class.getName();

  @Override
  public TypeStateMachineWeightFunctions getStateMachine() {
    return new FileMustBeClosedStateMachine();
  }

  @Test
  public void fluentOpen() {
    analyze(target, testName.getMethodName(), 1, 1);
  }

  @Test
  public void fluentOpenAndClose() {
    analyze(target, testName.getMethodName(), 2, 1);
  }
}
