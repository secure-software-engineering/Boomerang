package test.cases.typing;

import test.TestMethod;
import test.core.QueryMethods;
import test.core.selfrunning.AllocatedObject;

@SuppressWarnings("unused")
public class TypeConfusionTarget {

  @TestMethod
  public void invokesInterface() {
    B b = new B();
    A a1 = new A();
    Object o = b;
    A a = null;
    if (Math.random() > 0.5) {
      a = a1;
    } else {
      a = (A) o;
    }
    QueryMethods.queryFor(a);
  }

  private static class A implements AllocatedObject {}

  private static class B {}
}
