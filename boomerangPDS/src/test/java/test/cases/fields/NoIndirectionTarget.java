package test.cases.fields;

import test.TestMethod;
import test.core.QueryMethods;

@SuppressWarnings("unused")
public class NoIndirectionTarget {

  @TestMethod
  public void doubleWriteAndReadFieldPositive() {
    Object query = new Alloc();
    A a = new A();
    B b = new B();
    a.b = query;
    b.a = a;
    A c = b.a;
    Object alias = c.b;
    QueryMethods.queryFor(alias);
  }

  @TestMethod
  public void doubleWriteAndReadFieldNegative() {
    Object query = new Object();
    A a = new A();
    B b = new B();
    a.b = query;
    b.a = a;
    A c = b.a;
    Object alias = c.c;
    QueryMethods.unreachable(alias);
  }

  @TestMethod
  public void writeWithinCallPositive() {
    Alloc query = new Alloc();
    A a = new A();
    call(a, query);
    Object alias = a.b;
    QueryMethods.queryFor(alias);
  }

  @TestMethod
  public void writeWithinCallNegative() {
    Object query = new Object();
    A a = new A();
    call(a, query);
    Object alias = a.c;
    QueryMethods.unreachable(alias);
  }

  @TestMethod
  public void writeWithinCallSummarizedPositive() {
    Alloc query = new Alloc();
    A a = new A();
    call(a, query);
    Object alias = a.b;
    A b = new A();
    call(b, alias);
    Object summarizedAlias = b.b;
    QueryMethods.queryFor(summarizedAlias);
  }

  private void call(A a, Object query) {
    a.b = query;
  }

  @TestMethod
  public void doubleWriteWithinCallPositive() {
    Alloc query = new Alloc();
    A a = new A();
    B b = callAndReturn(a, query);
    A first = b.a;
    Object alias = first.b;
    QueryMethods.queryFor(alias);
  }

  private B callAndReturn(A a, Alloc query) {
    a.b = query;
    B b = new B();
    b.a = a;
    return b;
  }

  @TestMethod
  public void overwriteFieldTest() {
    Object query = new Object();
    A a = new A();
    a.b = query;
    a.b = null;
    Object alias = a.b;
    QueryMethods.unreachable(alias);
  }

  @TestMethod
  public void overwriteButPositiveFieldTest() {
    Alloc query = new Alloc();
    A a = new A();
    a.b = query;
    // a.c = null;
    Object alias = a.b;
    QueryMethods.queryFor(alias);
  }

  @TestMethod
  public void overwriteButPositiveFieldTest2() {
    Object query = new Object();
    int x = 0;
    A a = new A();
    a.b = query;
    a.b = null;
    int y = x;
    Object alias = a.b;
    QueryMethods.queryFor(alias);
  }
}
