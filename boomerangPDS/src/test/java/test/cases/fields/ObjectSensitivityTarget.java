package test.cases.fields;

import test.TestMethod;
import test.core.QueryMethods;

@SuppressWarnings("unused")
public class ObjectSensitivityTarget {

  @TestMethod
  public void objectSensitivity0() {
    B b1 = new B();
    Alloc b2 = new Alloc();

    A a1 = new A();
    A a2 = new A();

    a1.f = b1;
    a2.f = b2;
    Object b3 = a1.getF();
    int x = 1;
    Object b4 = a2.getF();
    // flow(b4);
    QueryMethods.queryFor(b4);
  }

  @TestMethod
  public void objectSensitivity1() {
    B b1 = new B();
    Alloc b2 = new Alloc();

    A a1 = new A(b1);
    A a2 = new A(b2);

    Object b3 = a1.getF();
    Object b4 = a2.getF();
    // flow(b4);
    QueryMethods.queryFor(b4);
  }

  private void flow(Object b3) {}

  @TestMethod
  public void objectSensitivity2() {
    Alloc b2 = new Alloc();
    A a2 = new A(b2);

    otherScope();
    Object b4 = a2.getF();

    QueryMethods.queryFor(b4);
  }

  private void otherScope() {
    B b1 = new B();
    A a1 = new A(b1);
    Object b3 = a1.getF();
  }

  public static class A {

    public Object f;

    public A(Object o) {
      this.f = o;
    }

    public A() {}

    public void setF(Object b2) {
      this.f = b2;
    }

    public Object getF() {
      return this.f;
    }
  }
}
