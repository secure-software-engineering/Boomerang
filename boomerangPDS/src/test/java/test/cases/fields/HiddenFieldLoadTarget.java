package test.cases.fields;

import test.TestMethod;
import test.core.QueryMethods;

@SuppressWarnings("unused")
public class HiddenFieldLoadTarget {

  @TestMethod
  public void run() {
    A b = new A();
    A a = b;
    b.setF();
    int x = 1;
    Object alias = a.f();
    QueryMethods.queryFor(alias);
  }

  @TestMethod
  public void run1() {
    A b = new A();
    A a = b;
    b.setF();
    int x = 1;
    Object alias = a.f;
    QueryMethods.queryFor(alias);
  }

  @TestMethod
  public void run7() {
    A b = new A();
    A a = b;
    b.setFBranched();
    int x = 1;
    Object alias = a.f;
    QueryMethods.queryFor(alias);
  }

  @TestMethod
  public void run3() {
    A b = new A();
    A a = b;
    Alloc alloc = new Alloc();
    b.setF(alloc);
    // int x =1;
    Object alias = a.f();
    QueryMethods.queryFor(alias);
  }

  @TestMethod
  public void run6() {
    A b = new A();
    A a = b;
    Alloc allocInRun6 = new Alloc();
    b.setF(allocInRun6);
    int x = 1;
    Object alias = a.f;
    QueryMethods.queryFor(alias);
  }

  @TestMethod
  public void run2() {
    A b = new A();
    A a = b;
    Alloc c = new Alloc();
    int y = 1;
    b.f = c;
    int x = 1;
    Object alias = a.f();
    QueryMethods.queryFor(alias);
  }

  @TestMethod
  public void run4() {
    A b = new A();
    A a = b;
    b.f = new Alloc();
    Object alias = a.f;
    QueryMethods.queryFor(alias);
  }

  private static class A {
    Object f;

    public void setF() {
      f = new Alloc();
    }

    public void setFBranched() {
      if (staticallyUnknown()) {
        f = new Alloc();
      } else {
        f = new Alloc();
      }
    }

    public boolean staticallyUnknown() {
      return Math.random() > 0.5;
    }

    public void setF(Object alloc) {
      f = alloc;
    }

    public Object f() {
      return f;
    }
  }
}
