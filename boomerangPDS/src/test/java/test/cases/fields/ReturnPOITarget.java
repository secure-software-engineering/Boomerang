/**
 * ***************************************************************************** 
 * Copyright (c) 2025 Fraunhofer IEM, Paderborn, Germany. This program and the
 * accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at http://www.eclipse.org/legal/epl-2.0.
 *
 * <p>SPDX-License-Identifier: EPL-2.0
 *
 * <p>Contributors: Johannes Spaeth - initial API and implementation
 * *****************************************************************************
 */
package test.cases.fields;

import test.TestMethod;
import test.core.QueryMethods;
import test.core.selfrunning.AllocatedObject;

@SuppressWarnings("unused")
public class ReturnPOITarget {

  public class A {
    B b;
  }

  public class B {
    C c;
  }

  public class C implements AllocatedObject {}

  @TestMethod
  public void indirectAllocationSite() {
    B a = new B();
    B e = a;
    allocation(a);
    C alias = e.c;
    C query = a.c;
    QueryMethods.queryFor(query);
  }

  private void allocation(B a) {
    C d = new C();
    a.c = d;
  }

  @TestMethod
  public void unbalancedReturnPOI1() {
    C a = new C();
    B b = new B();
    B c = b;
    setField(b, a);
    C alias = c.c;
    QueryMethods.queryFor(a);
  }

  private void setField(B a2, C a) {
    a2.c = a;
  }

  @TestMethod
  public void unbalancedReturnPOI3() {
    B b = new B();
    B c = b;
    setField(c);
    C query = c.c;
    QueryMethods.queryFor(query);
  }

  private void setField(B c) {
    c.c = new C();
  }

  @TestMethod
  public void whyRecursiveReturnPOIIsNecessary() {
    C c = new C();
    B b = new B();
    A a = new A();
    A a2 = a;
    a2.b = b;
    B b2 = b;
    setFieldTwo(a, c);
    C alias = a2.b.c;
    QueryMethods.queryFor(c);
  }

  @TestMethod
  public void whysRecursiveReturnPOIIsNecessary() {
    C c = new C();
    B b = new B();
    A a = new A();
    A a2 = a;
    a2.b = b;
    B b2 = b;
    setFieldTwo(a, c);
    C alias = a2.b.c;
    QueryMethods.queryFor(alias);
  }

  private void setFieldTwo(A b, C a) {
    b.b.c = a;
  }

  @TestMethod
  public void whysRecursiveReturnPOIIsNecessary3Addressed() {
    C x = new C();
    B y = new B();
    A z = new A();
    A aliasOuter = z;
    aliasOuter.b = y;
    setFieldTwo3Addresses(z, x);
    B l1 = aliasOuter.b;
    C alias = l1.c;
    QueryMethods.queryFor(alias);
  }

  private void setFieldTwo3Addresses(A base, C overwrite) {
    B loaded = base.b;
    loaded.c = overwrite;
  }
}
