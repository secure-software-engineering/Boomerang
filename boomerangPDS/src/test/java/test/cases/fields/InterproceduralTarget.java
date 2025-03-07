package test.cases.fields;

import test.TestMethod;
import test.core.QueryMethods;
import test.core.selfrunning.AllocatedObject;

@SuppressWarnings("unused")
public class InterproceduralTarget {

  @TestMethod
  public void test3() {
    A a = new A();
    B b = new B();
    b.c = new C();
    alias(a, b);
    B h = a.b;
    C query = h.c;
    QueryMethods.queryFor(query);
  }

  private void alias(A a, B b) {
    a.b = b;
  }

  public static class A {
    B b;
  }

  public static class B {
    C c;
  }

  public static class C implements AllocatedObject {}
}
