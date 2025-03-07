package test.cases.fields;

import test.TestMethod;
import test.core.QueryMethods;
import test.core.selfrunning.AllocatedObject;

@SuppressWarnings("unused")
public class IntraproceduralStrongUpdateTarget {

  @TestMethod
  public void strongUpdateWithField() {
    A a = new A();
    a.field = new Object();
    A b = a;
    b.field = new AllocatedObject() {};
    Object alias = a.field;
    QueryMethods.queryFor(alias);
  }

  @TestMethod
  public void strongUpdateWithFieldSwapped() {
    A a = new A();
    A b = a;
    b.field = new Object();
    a.field = new AllocatedObject() {};
    Object alias = a.field;
    QueryMethods.queryFor(alias);
  }

  private class A {
    Object field;
  }

  @TestMethod
  public void innerClass() {
    A a = new A();
    A b = a;
    b.field = new I();
    Object alias = a.field;
    QueryMethods.queryFor(alias);
  }

  private class I implements AllocatedObject {}

  private static class B {
    Object field;
  }

  @TestMethod
  public void anonymousClass() {
    B a = new B();
    B b = a;
    b.field = new AllocatedObject() {};
    Object alias = a.field;
    QueryMethods.queryFor(alias);
  }
}
