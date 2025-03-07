package typestate.tests;

import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import test.IDEALTestingFramework;
import typestate.finiteautomata.TypeStateMachineWeightFunctions;
import typestate.impl.statemachines.InputStreamStateMachine;
import typestate.targets.InputStreamLong;

public class InputStreamLongTest extends IDEALTestingFramework {

  private final String target = InputStreamLong.class.getName();

  @Override
  protected TypeStateMachineWeightFunctions getStateMachine() {
    return new InputStreamStateMachine();
  }

  @Override
  protected List<String> getIncludedPackages() {
    return List.of("java.io.FileInputStream");
  }

  @Override
  protected List<String> getExcludedPackages() {
    return new ArrayList<>();
  }

  @Test
  public void test1() {
    analyze(target, testName.getMethodName(), 1, 1);
  }

  @Test
  public void test2() {
    analyze(target, testName.getMethodName(), 1, 2);
  }

  @Test
  public void test3() {
    analyze(target, testName.getMethodName(), 1, 1);
  }
}
