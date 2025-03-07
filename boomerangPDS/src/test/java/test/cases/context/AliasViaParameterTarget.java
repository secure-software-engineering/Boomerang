package test.cases.context;

import test.TestMethod;
import test.core.QueryMethods;
import test.core.selfrunning.AllocatedObject;

@SuppressWarnings("unused")
public class AliasViaParameterTarget {

  @TestMethod
  public void aliasViaParameter() {
    A a = new A();
    A b = a;
    setAndLoadFieldOnAlias(a, b);
    AllocatedObject query = a.field;
    QueryMethods.queryFor(query);
  }

  @TestMethod
  public void aliasViaParameterWrapped() {
    A a = new A();
    A b = a;
    passThrough(a, b);
    AllocatedObject query = a.field;
    QueryMethods.queryFor(query);
  }

  private void passThrough(A a, A b) {
    setAndLoadFieldOnAlias(a, b);
  }

  private void setAndLoadFieldOnAlias(A a, A b) {
    b.field = new AllocatedObject() {};
  }

  public static class A {
    AllocatedObject field;
  }
}
