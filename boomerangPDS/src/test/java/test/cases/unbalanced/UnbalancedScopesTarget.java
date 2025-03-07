package test.cases.unbalanced;

import test.TestMethod;
import test.cases.fields.Alloc;
import test.cases.fields.B;
import test.core.QueryMethods;

@SuppressWarnings("unused")
public class UnbalancedScopesTarget {

  private boolean staticallyUnknown() {
    return Math.random() > 0.5;
  }

  @TestMethod
  public void closingContext() {
    Object object = create();
    QueryMethods.queryFor(object);
  }

  @TestMethod
  public void openingContext() {
    Object object = create();
    Object y = object;
    inner(y);
  }

  @TestMethod
  public void doubleClosingContext() {
    Object object = wrappedCreate();
    QueryMethods.queryFor(object);
  }

  @TestMethod
  public void branchedReturn() {
    Object object = aOrB();
    QueryMethods.queryFor(object);
  }

  @TestMethod
  public void summaryReuse() {
    Object object = createA();
    Object y = object;
    Object x = id(y);
    QueryMethods.queryFor(x);
  }

  private Object createA() {
    Alloc c = new Alloc();
    Object d = id(c);
    return d;
  }

  private Object id(Object c) {
    return c;
  }

  private Object aOrB() {
    if (staticallyUnknown()) {
      return new Alloc();
    }
    return new B();
  }

  public Object wrappedCreate() {
    if (staticallyUnknown()) return create();
    return wrappedCreate();
  }

  private void inner(Object inner) {
    Object x = inner;
    QueryMethods.queryFor(x);
  }

  private Object create() {
    return new Alloc();
  }
}
