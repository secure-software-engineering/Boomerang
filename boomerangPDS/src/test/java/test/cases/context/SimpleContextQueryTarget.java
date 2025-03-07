package test.cases.context;

import test.TestMethod;
import test.cases.fields.Alloc;
import test.core.QueryMethods;
import test.core.selfrunning.AllocatedObject;

@SuppressWarnings("unused")
public class SimpleContextQueryTarget {

  @TestMethod
  public void outerAllocation() {
    AllocatedObject alloc = new Alloc();
    methodOfQuery(alloc);
  }

  private void methodOfQuery(AllocatedObject allocInner) {
    AllocatedObject alias = allocInner;
    QueryMethods.queryFor(alias);
  }

  @TestMethod
  public void outerAllocation2() {
    AllocatedObject alloc = new AllocatedObject() {};
    AllocatedObject same = alloc;
    methodOfQuery(alloc, same);
  }

  @TestMethod
  public void outerAllocation3() {
    AllocatedObject alloc = new AllocatedObject() {};
    Object same = new Object();
    methodOfQuery(alloc, same);
  }

  private void methodOfQuery(Object alloc, Object alias) {
    QueryMethods.queryFor(alloc);
  }
}
