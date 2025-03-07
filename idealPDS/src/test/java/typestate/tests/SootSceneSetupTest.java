package typestate.tests;

import java.util.List;
import org.junit.Test;
import test.IDEALTestingFramework;
import typestate.finiteautomata.TypeStateMachineWeightFunctions;
import typestate.impl.statemachines.FileMustBeClosedStateMachineCallToReturn;
import typestate.targets.SootSceneSetup;
import typestate.targets.helper.File;

public class SootSceneSetupTest extends IDEALTestingFramework {

  private final String target = SootSceneSetup.class.getName();

  @Override
  protected TypeStateMachineWeightFunctions getStateMachine() {
    return new FileMustBeClosedStateMachineCallToReturn();
  }

  @Override
  public List<String> getExcludedPackages() {
    return List.of(File.class.getName());
  }

  @Test
  public void simple() {
    analyze(target, testName.getMethodName(), 2, 1);
  }

  @Test
  public void aliasSimple() {
    analyze(target, testName.getMethodName(), 2, 1);
  }
}
