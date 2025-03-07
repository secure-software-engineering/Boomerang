package test.cases.basic;

import test.TestMethod;
import test.cases.fields.Alloc;
import test.core.QueryMethods;

@SuppressWarnings("unused")
public class UnbalancedInterproceduralTarget {

  @TestMethod
  public void unbalancedCreation() {
    Alloc alias1 = create();
    Alloc query = alias1;
    QueryMethods.queryFor(query);
  }

  @TestMethod
  public void doubleUnbalancedCreation() {
    Alloc alias1 = wrappedCreate();
    Alloc query = alias1;
    QueryMethods.queryFor(query);
  }

  private Alloc wrappedCreate() {
    return create();
  }

  private Alloc create() {
    return new Alloc();
  }
}
