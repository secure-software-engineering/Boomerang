package test.aliasing.pointerbench;

import org.junit.Test;
import target.aliasing.PointerBench.basic.*;
import test.aliasing.AliasingTestSetUp;

public class BasicTest extends AliasingTestSetUp {
  @Test
  public void branching1() {
    String queryLHS = "a_q1";
    String targetClass = Branching1.class.getName();
    runAnalyses(queryLHS, targetClass, null);
  }

  @Test
  public void interprocedural1() {
    String queryLHS = "x_q1";
    String targetClass = Interprocedural1.class.getName();
    runAnalyses(queryLHS, targetClass, null);
  }

  @Test
  public void interprocedural2() {
    String queryLHS = "y_q1";
    String targetClass = Interprocedural2.class.getName();
    runAnalyses(queryLHS, targetClass, null);
  }

  @Test
  public void loops1() {
    String queryLHS = "node_q1";
    String targetClass = Loops1.class.getName();
    String targetMetod = "test";
    runAnalyses(queryLHS, targetClass, targetMetod);
  }

  @Test
  public void loops2() {
    String queryLHS = "node_q1";
    String targetClass = Loops2.class.getName();
    String targetMetod = "test";
    runAnalyses(queryLHS, targetClass, targetMetod);
  }

  @Test
  public void parameter1() {
    String queryLHS = "b_q1";
    String targetClass = Parameter1.class.getName();
    String targetMetod = "test";
    runAnalyses(queryLHS, targetClass, targetMetod);
  }

  @Test
  public void parameter2() {
    String queryLHS = "b_q1";
    String targetClass = Parameter2.class.getName();
    String targetMetod = "test";
    runAnalyses(queryLHS, targetClass, targetMetod);
  }

  @Test
  public void recursion1() {
    String queryLHS = "n_q1";
    String targetClass = Recursion1.class.getName();
    String targetMetod = "test";
    runAnalyses(queryLHS, targetClass, targetMetod);
  }

  @Test
  public void returnValue1() {
    String queryLHS = "b_q1";
    String targetClass = ReturnValue1.class.getName();
    runAnalyses(queryLHS, targetClass, null);
  }

  @Test
  public void returnValue2() {
    String queryLHS = "b_q1";
    String targetClass = ReturnValue2.class.getName();
    runAnalyses(queryLHS, targetClass, null);
  }

  @Test
  public void returnValue3() {
    String queryLHS = "x_q1";
    String targetClass = ReturnValue3.class.getName();
    runAnalyses(queryLHS, targetClass, null);
  }

  @Test
  public void simpleAlias1() {
    String queryLHS = "b_q1";
    String targetClass = SimpleAlias1.class.getName();
    runAnalyses(queryLHS, targetClass, null);
  }
}
