package typestate.tests;

import org.junit.Ignore;
import org.junit.Test;
import test.IDEALTestingFramework;
import typestate.finiteautomata.TypeStateMachineWeightFunctions;
import typestate.impl.statemachines.SocketStateMachine;
import typestate.targets.SocketLong;

public class SocketLongTest extends IDEALTestingFramework {

  private final String target = SocketLong.class.getName();

  @Override
  protected TypeStateMachineWeightFunctions getStateMachine() {
    return new SocketStateMachine();
  }

  @Test
  public void test1() {
    analyze(target, testName.getMethodName(), 1, 1);
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
  @Ignore("Reading sockets from an iterator is too complex")
  public void test4() {
    analyze(target, testName.getMethodName(), 1, 2);
  }

  @Test
  @Ignore("Reading sockets from an iterator is too complex")
  public void test5() {
    analyze(target, testName.getMethodName(), 1, 1);
  }
}
