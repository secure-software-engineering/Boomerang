package typestate.tests;

import org.junit.Ignore;
import org.junit.Test;
import test.IDEALTestingFramework;
import typestate.finiteautomata.TypeStateMachineWeightFunctions;
import typestate.impl.statemachines.FileMustBeClosedStateMachine;
import typestate.targets.FileMustBeClosed;

public class FileMustBeClosedTest extends IDEALTestingFramework {

  private final String target = FileMustBeClosed.class.getName();

  @Override
  public TypeStateMachineWeightFunctions getStateMachine() {
    return new FileMustBeClosedStateMachine();
  }

  @Test
  public void simple() {
    analyze(target, testName.getMethodName(), 2, 1);
  }

  @Test
  public void simple2() {
    analyze(target, testName.getMethodName(), 1, 1);
  }

  @Test
  public void simple0() {
    analyze(target, testName.getMethodName(), 2, 1);
  }

  @Test
  public void simple0a() {
    analyze(target, testName.getMethodName(), 2, 1);
  }

  @Test
  public void simpleStrongUpdate() {
    analyze(target, testName.getMethodName(), 2, 1);
  }

  @Test
  public void simpleStrongUpdate1() {
    analyze(target, testName.getMethodName(), 1, 1);
  }

  @Test
  public void simpleStrongUpdate1a() {
    analyze(target, testName.getMethodName(), 2, 1);
  }

  @Test
  public void simpleStrongUpdate2() {
    analyze(target, testName.getMethodName(), 2, 1);
  }

  @Test
  public void recursion() {
    analyze(target, testName.getMethodName(), 2, 1);
  }

  @Test
  public void simple1() {
    analyze(target, testName.getMethodName(), 2, 1);
  }

  @Test
  public void simpleNoStrongUpdate() {
    analyze(target, testName.getMethodName(), 3, 2);
  }

  @Test
  public void branching() {
    analyze(target, testName.getMethodName(), 2, 1);
  }

  @Test
  public void test222() {
    analyze(target, testName.getMethodName(), 1, 1);
  }

  @Test
  public void branchingMay() {
    analyze(target, testName.getMethodName(), 2, 1);
  }

  @Test
  public void continued() {
    analyze(target, testName.getMethodName(), 3, 1);
  }

  @Test
  public void aliasing() {
    analyze(target, testName.getMethodName(), 3, 1);
  }

  @Test
  public void summaryTest() {
    analyze(target, testName.getMethodName(), 3, 2);
  }

  @Test
  public void simpleAlias() {
    analyze(target, testName.getMethodName(), 3, 1);
  }

  @Test
  public void wrappedOpenCall() {
    analyze(target, testName.getMethodName(), 2, 1);
  }

  @Test
  public void interprocedural() {
    analyze(target, testName.getMethodName(), 2, 1);
  }

  @Test
  public void interprocedural2() {
    analyze(target, testName.getMethodName(), 1, 1);
  }

  @Test
  public void intraprocedural() {
    analyze(target, testName.getMethodName(), 2, 1);
  }

  @Test
  public void flowViaField() {
    analyze(target, testName.getMethodName(), 1, 1);
  }

  @Test
  public void flowViaFieldDirect() {
    analyze(target, testName.getMethodName(), 1, 1);
  }

  @Test
  public void flowViaFieldDirect2() {
    analyze(target, testName.getMethodName(), 2, 1);
  }

  @Test
  public void flowViaFieldNotUnbalanced() {
    analyze(target, testName.getMethodName(), 3, 1);
  }

  @Test
  public void indirectFlow() {
    analyze(target, testName.getMethodName(), 3, 1);
  }

  @Test
  public void parameterAlias() {
    analyze(target, testName.getMethodName(), 3, 1);
  }

  @Test
  public void parameterAlias2() {
    analyze(target, testName.getMethodName(), 2, 1);
  }

  @Test
  public void aliasInInnerScope() {
    analyze(target, testName.getMethodName(), 2, 1);
  }

  @Test
  public void noStrongUpdate() {
    analyze(target, testName.getMethodName(), 1, 1);
  }

  @Test
  public void unbalancedReturn1() {
    analyze(target, testName.getMethodName(), 2, 1);
  }

  @Test
  @Ignore("Requires two seeds but finds only one")
  public void unbalancedReturn2() {
    analyze(target, testName.getMethodName(), 3, 2);
  }

  @Test
  public void unbalancedReturnAndBalanced() {
    analyze(target, testName.getMethodName(), 2, 1);
  }

  @Test
  public void lateWriteToField() {
    analyze(target, testName.getMethodName(), 2, 1);
  }

  @Test
  public void fieldStoreAndLoad1() {
    analyze(target, testName.getMethodName(), 2, 1);
  }

  @Test
  @Ignore("Expects two seeds but finds only 1")
  public void fieldStoreAndLoad2() {
    analyze(target, testName.getMethodName(), 3, 2);
  }

  @Test
  public void wrappedClose() {
    analyze(target, testName.getMethodName(), 4, 1);
  }

  @Test
  public void wrappedClose2() {
    analyze(target, testName.getMethodName(), 2, 1);
  }

  @Test
  public void wrappedOpen2() {
    analyze(target, testName.getMethodName(), 1, 1);
  }

  @Test
  public void wrappedClose1() {
    analyze(target, testName.getMethodName(), 2, 1);
  }

  @Test
  public void wrappedOpen() {
    analyze(target, testName.getMethodName(), 1, 1);
  }

  @Test
  public void multipleStates() {
    analyze(target, testName.getMethodName(), 4, 1);
  }

  @Test
  public void doubleBranching() {
    analyze(target, testName.getMethodName(), 1, 1);
  }

  @Test
  public void whileLoopBranching() {
    analyze(target, testName.getMethodName(), 1, 1);
  }

  @Test
  @Ignore
  public void staticFlow() {
    analyze(target, testName.getMethodName(), 2, 1);
  }

  @Test
  public void staticFlowSimple() {
    analyze(target, testName.getMethodName(), 1, 1);
  }

  @Test
  public void storedInObject() {
    analyze(target, testName.getMethodName(), 1, 1);
  }

  @Test
  public void storedInObject2() {
    analyze(target, testName.getMethodName(), 4, 1);
  }
}
