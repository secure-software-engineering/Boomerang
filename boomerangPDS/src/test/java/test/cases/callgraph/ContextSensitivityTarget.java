package test.cases.callgraph;

import test.TestMethod;
import test.cases.fields.Alloc;
import test.core.QueryMethods;

@SuppressWarnings("unused")
public class ContextSensitivityTarget {

  public void wrongContext() {
    SuperClass type = new WrongSubclass();
    method(type);
  }

  public Object method(SuperClass type) {
    Alloc alloc = new Alloc();
    type.foo(alloc);
    return alloc;
  }

  @TestMethod
  public void testOnlyCorrectContextInCallGraph() {
    wrongContext();
    SuperClass type = new CorrectSubclass();
    Object alloc = method(type);
    QueryMethods.queryFor(alloc);
  }

  public class SuperClass {

    public void foo(Object o) {
      QueryMethods.unreachable(o);
    }
  }

  class CorrectSubclass extends SuperClass {
    public void foo(Object o) {}
  }

  class WrongSubclass extends SuperClass {

    public void foo(Object o) {
      QueryMethods.unreachable(o);
    }
  }
}
