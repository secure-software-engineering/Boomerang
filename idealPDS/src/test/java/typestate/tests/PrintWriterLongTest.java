package typestate.tests;

import java.io.FileNotFoundException;
import java.util.List;
import org.junit.Test;
import test.IDEALTestingFramework;
import typestate.finiteautomata.TypeStateMachineWeightFunctions;
import typestate.impl.statemachines.PrintWriterStateMachine;
import typestate.targets.PrintWriterLong;

public class PrintWriterLongTest extends IDEALTestingFramework {

  private final String target = PrintWriterLong.class.getName();

  @Override
  protected List<String> getIncludedPackages() {
    return List.of("java.io.PrintWriter");
  }

  @Override
  protected TypeStateMachineWeightFunctions getStateMachine() {
    return new PrintWriterStateMachine();
  }

  @Test
  public void test1() throws FileNotFoundException {
    analyze(target, testName.getMethodName(), 1, 1);
  }
}
