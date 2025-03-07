package test.cases.multiqueries;

import test.TestMethod;
import test.core.QueryMethods;
import test.core.selfrunning.AllocatedObject;
import test.core.selfrunning.AllocatedObject2;

@SuppressWarnings("unused")
public class MultiQueryTarget {

  @TestMethod
  public void twoQueriesTest() {
    Object alloc1 = new Alloc1();
    Object alias1 = new Alloc2();
    Object query = alloc1;
    QueryMethods.queryFor1(query, AllocatedObject.class);
    QueryMethods.queryFor2(alias1, AllocatedObject2.class);
  }

  @TestMethod
  public void withFieldsTest() {
    Alloc1 alloc1 = new Alloc1();
    Object alias1 = new Alloc2();
    Alloc1 alias = alloc1;
    alias.field = alias1;
    Object query = alloc1.field;
    QueryMethods.queryFor1(alias, AllocatedObject.class);
    QueryMethods.queryFor2(query, AllocatedObject2.class);
  }

  private static class Alloc1 implements AllocatedObject {
    Object field = new Object();
  }

  private static class Alloc2 implements AllocatedObject2 {}
}
