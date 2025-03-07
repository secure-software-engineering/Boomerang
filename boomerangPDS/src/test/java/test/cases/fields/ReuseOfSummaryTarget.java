package test.cases.fields;

import test.TestMethod;
import test.core.QueryMethods;

@SuppressWarnings("unused")
public class ReuseOfSummaryTarget {

  @TestMethod
  public void summaryTest() {
    A a = new A();
    A b = new A();

    Object c = new Alloc(); // o1
    foo(a, b, c);
    foo(a, a, c);

    /*
     * the test case extracts all allocated object of type Alloc and assumes these objects to flow
     * as argument to queryFor(var). In this example var and a.f point to o1
     */
    Object var = a.f;
    QueryMethods.queryFor(var);
  }

  private void foo(A c, A d, Object f) {
    d.f = f;
  }

  private static class A {
    Object f;

    public A() {}
  }
}
