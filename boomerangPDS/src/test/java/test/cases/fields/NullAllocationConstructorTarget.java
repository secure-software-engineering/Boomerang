package test.cases.fields;

import test.TestMethod;
import test.core.QueryMethods;

@SuppressWarnings("unused")
public class NullAllocationConstructorTarget {

  private class A {
    B f = null;
  }

  private class B {}

  @TestMethod
  public void nullAllocationOfField() {
    A a = new A();
    B variable = a.f;
    QueryMethods.queryFor(variable);
  }
}
