package typestate.tests;

import org.junit.Test;
import test.IDEALTestingFramework;
import typestate.finiteautomata.TypeStateMachineWeightFunctions;
import typestate.impl.statemachines.FileMustBeClosedStateMachine;
import typestate.targets.FileMustBeClosedStrongUpdate;

public class FileMustBeClosedStrongUpdateTest extends IDEALTestingFramework {

  private final String target = FileMustBeClosedStrongUpdate.class.getName();

  @Override
  public TypeStateMachineWeightFunctions getStateMachine() {
    return new FileMustBeClosedStateMachine();
  }

  @Test
  public void noStrongUpdatePossible() {
    analyze(target, testName.getMethodName(), 2, 2);
  }

  @Test
  public void aliasSensitive() {
    analyze(target, testName.getMethodName(), 2, 1);
  }
}
