package test.cases.fields;

import test.TestMethod;
import test.core.QueryMethods;
import test.core.selfrunning.AllocatedObject;

@SuppressWarnings("unused")
public class MeetPOITarget {

  @TestMethod
  public void wrappedAlloc() {
    A e = new A();
    A g = e;
    wrapper(g);
    C h = e.b.c;
    QueryMethods.queryFor(h);
  }

  private void wrapper(A g) {
    alloc(g);
  }

  private void alloc(A g) {
    g.b.c = new C();
  }

  public class A {
    B b = new B();
  }

  public class B {
    C c;
  }

  public class C implements AllocatedObject {
    String g;
  }
}
