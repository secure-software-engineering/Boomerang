/**
 * ***************************************************************************** 
 * Copyright (c) 2018 Fraunhofer IEM, Paderborn, Germany
 * <p>
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * <p>
 * SPDX-License-Identifier: EPL-2.0
 * <p>
 * Contributors:
 *   Johannes Spaeth - initial API and implementation
 * *****************************************************************************
 */
package test.cases.fields;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import test.core.BoomerangTestRunnerInterceptor;
import test.core.QueryMethods;
import test.core.selfrunning.AllocatedObject;

@ExtendWith(BoomerangTestRunnerInterceptor.class)
public class CallPOITest {

  public static class A {
    B b = new B();
  }

  public static class B {
    AllocObj c; // = new AllocObj();
  }

  public static class AllocObj implements AllocatedObject {}

  @Test
  public void simpleButDiffer() {
    FieldAlloc c = new FieldAlloc();
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

  @Test
  public void indirectAllocationSite3Address() {
    A a = new A();
    allocation(a);
    B load = a.b;
    AllocObj alias = load.c;
    QueryMethods.queryFor(alias);
  }

  @Test
  public void indirectAllocationSiteViaParameter() {
    A a = new A();
    AllocObj alloc = new AllocObj();
    allocation(a, alloc);
    AllocObj alias = a.b.c;
    QueryMethods.queryFor(alias);
  }

  @Test
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

  @Test
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

  @Test
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

  @Test
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

  @Test
  public void innerSetFieldOnAlias() {
    Outer o = new Outer();
    set(o);
    Object alias = o.field;
    QueryMethods.queryFor(alias);
  }

  private void set(Outer o) {
    FieldAlloc alloc = new FieldAlloc();
    Outer alias = o;
    alias.field = alloc;
  }

  public static class Outer {
    Object field;
  }

  @Test
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

  @Test
  public void testForBackwardCallPOI() {
    // Thanks to Martin Mory for contributing the test.
    FieldAlloc v = new FieldAlloc();
    Object x = front(v);
    QueryMethods.queryFor(x);
  }

  public static Object fromIt(SomeObj it) {
    FieldAlloc x = it.f;
    return x;
  }

  public static Object front(FieldAlloc y) {
    SomeObj it = new SomeObj();
    SomeObj it2 = it;
    it2.f = y;
    Object f = fromIt(it);
    return f;
  }

  public static class SomeObj {
    FieldAlloc f;
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
