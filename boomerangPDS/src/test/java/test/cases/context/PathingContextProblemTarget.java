package test.cases.context;

import test.TestMethod;
import test.cases.basic.Allocation;
import test.core.QueryMethods;

@SuppressWarnings("unused")
public class PathingContextProblemTarget {

  @TestMethod
  public void start() {
    Inner i = new Inner();
    i.test1();
    i.test2();
  }

  public static class Inner {

    public void callee(Object a, Object b) {
      QueryMethods.queryFor(a);
    }

    public void test1() {
      Object a1 = new Allocation();
      Object b1 = a1;
      callee(a1, b1);
    }

    public void test2() {
      Object a2 = new Allocation();
      Object b2 = new Object();
      callee(a2, b2);
    }
  }

  @TestMethod
  public void start2() {
    Inner i = new Inner();
    i.test1();
    i.test2();
  }

  public static class Inner2 {

    public void callee(Object a, Object b) {
      QueryMethods.queryFor(b);
    }

    public void test1() {
      Object a1 = new Allocation();
      Object b1 = a1;
      callee(a1, b1);
    }

    public void test2() {
      Object a2 = new Object();
      Object b2 = new Allocation();
      callee(a2, b2);
    }
  }
}
