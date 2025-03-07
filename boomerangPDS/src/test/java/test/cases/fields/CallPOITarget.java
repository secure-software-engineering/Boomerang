package test.cases.fields;

import test.TestMethod;
import test.core.QueryMethods;
import test.core.selfrunning.AllocatedObject;

@SuppressWarnings("unused")
public class CallPOITarget {

  public static class A {
    B b = new B();
  }

  public static class B {
    AllocObj c; // = new AllocObj();
  }

  public static class AllocObj implements AllocatedObject {}

  @TestMethod
  public void simpleButDiffer() {
    Alloc c = new Alloc();
    T t = new T(c);
    S s = new S();
    t.foo(s);
    Object q = s.get();
    QueryMethods.queryFor(q);
  }

  public static class T {
    private final Object value;

    T(Object o) {
      this.value = o;
    }

    public void foo(S s) {
      Object val = this.value;
      s.set(val);
    }
  }

  public static class S {
    private Object valueInS;

    public void set(Object val) {
      this.valueInS = val;
    }

    public Object get() {
      Object alias = this.valueInS;
      return alias;
    }
  }

  private static void allocation(A a) {
    B intermediate = a.b;
    AllocObj e = new AllocObj();
    AllocObj d = e;
    intermediate.c = d;
  }

  @TestMethod
  public void indirectAllocationSite3Address() {
    A a = new A();
    allocation(a);
    B load = a.b;
    AllocObj alias = load.c;
    QueryMethods.queryFor(alias);
  }

  @TestMethod
  public void indirectAllocationSiteViaParameter() {
    A a = new A();
    AllocObj alloc = new AllocObj();
    allocation(a, alloc);
    AllocObj alias = a.b.c;
    QueryMethods.queryFor(alias);
  }

  @TestMethod
  public void indirectAllocationSiteViaParameterAliased() {
    A a = new A();
    // a.b = new B();
    AllocObj alloc = new AllocObj();
    A b = a;
    allocation(a, alloc);
    B loadedFromB = b.b;
    AllocObj alias = loadedFromB.c;
    QueryMethods.queryFor(alias);
  }

  private void allocation(A a, AllocObj d) {
    B intermediate = a.b;
    intermediate.c = d;
  }

  @TestMethod
  public void whyRecursiveCallPOIIsNecessary() {
    // TODO This test case seems to be non deterministic, why?
    A a = new A();
    AllocObj alloc = new AllocObj();
    A b = a;
    allocationIndirect(a, alloc);
    B loadedFromB = b.b;
    AllocObj alias = loadedFromB.c;
    QueryMethods.queryFor(alias);
  }

  @TestMethod
  public void whyRecursiveCallPOIIsNecessarySimpler() {
    A a = new A();
    AllocObj alloc = new AllocObj();
    allocationIndirect(a, alloc);
    AllocObj alias = a.b.c;
    QueryMethods.queryFor(alias);
  }

  private void allocationIndirect(A innerA, AllocObj d) {
    B innerB = new B();
    A a2 = innerA;
    a2.b = innerB;
    B intermediate = a2.b;
    intermediate.c = d;
    AllocObj AndMe = innerA.b.c;
  }

  @TestMethod
  public void whyRecursiveCallPOIIsNecessarySimpler2() {
    A a = new A();
    AllocObj alloc = new AllocObj();
    allocationIndirect2(a, alloc);
  }

  private void allocationIndirect2(A whereAmI, AllocObj d) {
    B b = new B();
    A a2 = whereAmI;
    a2.b = b;
    B intermediate = a2.b;
    intermediate.c = d;
    AllocObj AndMe = whereAmI.b.c;
    QueryMethods.queryFor(AndMe);
  }

  @TestMethod
  public void innerSetFieldOnAlias() {
    Outer o = new Outer();
    set(o);
    Object alias = o.field;
    QueryMethods.queryFor(alias);
  }

  private void set(Outer o) {
    Alloc alloc = new Alloc();
    Outer alias = o;
    alias.field = alloc;
  }

  public static class Outer {
    Object field;
  }

  @TestMethod
  public void indirectAllocationSiteViaParameterAliasedNoPreAllocs() {
    A1 a = new A1();
    a.b = new B1();
    AllocObj1 alloc = new AllocObj1();
    A1 b = a;
    allocation(a, alloc);
    B1 loadedFromB = b.b;
    AllocObj1 alias = loadedFromB.c;
    QueryMethods.queryFor(alias);
  }

  @TestMethod
  public void testForBackwardCallPOI() {
    // Thanks to Martin Mory for contributing the test.
    Alloc v = new Alloc();
    Object x = front(v);
    QueryMethods.queryFor(x);
  }

  public static Object fromIt(SomeObj it) {
    Alloc x = it.f;
    return x;
  }

  public static Object front(Alloc y) {
    SomeObj it = new SomeObj();
    SomeObj it2 = it;
    it2.f = y;
    Object f = fromIt(it);
    return f;
  }

  public static class SomeObj {
    Alloc f;
  }

  private void allocation(A1 a, AllocObj1 d) {
    B1 intermediate = a.b;
    intermediate.c = d;
  }

  public static class A1 {
    B1 b;
  }

  public static class B1 {
    AllocObj1 c;
  }

  public static class AllocObj1 implements AllocatedObject {}
}
