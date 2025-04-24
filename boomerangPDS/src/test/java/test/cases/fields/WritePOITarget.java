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

import test.TestMethod;
import test.core.QueryMethods;
import test.core.selfrunning.AllocatedObject;

@SuppressWarnings("unused")
public class WritePOITarget {

  public static class A {
    Object b = null;
    // Alloc c = null;
  }

  public static class I {
    Object b; // = new Object();
  }

  @TestMethod
  public void overwrite() {
    I a = new I();
    a.b = new Object();
    a.b = new Alloc();
    Object alias = a.b;
    QueryMethods.queryFor(alias);
  }

  @TestMethod
  public void indirectAllocationSite12() {
    A a = new A();
    setField(a);
    Object alias = a.b;
    QueryMethods.queryFor(alias);
  }

  private void setField(A a) {
    A a1 = a;
    Alloc alloc = new Alloc();
    a1.b = alloc;
  }

  @TestMethod
  public void indirectAllocationSite() {
    Alloc query = new Alloc();
    A a = new A();
    A e = a;
    e.b = new Object();
    a.b = query;
    Object alias = e.b;
    QueryMethods.queryFor(alias);
  }

  @TestMethod
  public void indirectAllocationSite1() {
    Alloc query = new Alloc();
    A a = new A();
    A e = a;
    a.b = query;
    Object alias = e.b;
    QueryMethods.queryFor(alias);
  }

  @TestMethod
  public void indirectAllocationSite144() {
    Alloc q = new Alloc();
    A a = new A();
    A e = a;
    a.b = q;
    // Object alias = e.b;
    QueryMethods.queryFor(q);
  }

  @TestMethod
  public void indirectAllocationSite2() {
    Alloc query = new Alloc();
    A a = new A();
    A e = a;
    a.b = query;
    int x = 1;
    Object alias = e.b;
    QueryMethods.queryFor(alias);
  }

  @TestMethod
  public void indirectAllocationSite3() {
    Alloc query = new Alloc();
    A a = new A();
    A e = a;
    a.b = query;
    QueryMethods.queryFor(query);
  }

  public static class Level1 {
    Level2 l2 = new Level2();
  }

  public static class Level2 {
    Alloc a;
  }

  @TestMethod
  public void doubleIndirectAllocationSite() {
    Level1 base = new Level1();

    Alloc query = new Alloc();
    Level2 level2 = new Level2();
    base.l2 = level2;
    level2.a = query;
    Level2 intermediat = base.l2;
    Alloc samesame = intermediat.a;
    QueryMethods.queryFor(samesame);
  }

  @TestMethod
  public void doubleIndirectAllocationSiteSIMPLE() {
    Level1 base = new Level1();

    Alloc query = new Alloc();
    Level2 level2 = new Level2();
    base.l2 = level2;
    level2.a = query;
    QueryMethods.queryFor(query);
  }

  @TestMethod
  public void simpleIndirectAllocationSite() {
    Level1 base = new Level1();

    Alloc query = new Alloc();
    Level2 level2 = new Level2();
    base.l2 = level2;
    level2.a = query;
    QueryMethods.queryFor(query);
  }

  @TestMethod
  public void doubleIndirectAllocationSiteMoreComplex() {
    Level1 base = new Level1();
    Level1 baseAlias = base;

    Alloc query = new Alloc();
    Level2 level2 = new Level2();
    base.l2 = level2;
    level2.a = query;
    Level2 alias = baseAlias.l2;
    Alloc samesame = alias.a;
    QueryMethods.queryFor(samesame);
  }

  @TestMethod
  public void directAllocationSite() {
    Alloc query = new Alloc();
    A a = new A();
    a.b = query;
    Object alias = a.b;
    QueryMethods.queryFor(alias);
  }

  @TestMethod
  public void directAllocationSiteSimpler() {
    Alloc query = new Alloc();
    A a = new A();
    a.b = query;
    QueryMethods.queryFor(query);
  }

  @TestMethod
  public void loadTwice() {
    Alloc alloc = new Alloc();
    A a = new A();
    a.b = alloc;
    Object query1 = a.b;
    Object query2 = a.b;
    QueryMethods.queryFor(query2);
  }

  @TestMethod
  public void overwriteTwice() {
    // TODO This test case should not be imprecise, but is imprecise. Jimple introduces additional
    // variable, why?.
    Alloc alloc = new Alloc();
    A a = new A();
    a.b = new Object();
    a.b = alloc;
    int x = 1;
    Object query1 = a.b;
    QueryMethods.queryFor(query1);
  }

  @TestMethod
  public void overwriteWithinCall() {
    Alloc alloc = new Alloc();
    A a = new A();
    set(a);
    a.b = alloc;
    int x = 1;
    Object query1 = a.b;
    QueryMethods.queryFor(query1);
  }

  private void set(A a) {
    a.b = new Object();
  }

  @TestMethod
  public void overwriteTwiceStrongAliased() {
    // This test case is expected to be imprecise.
    Alloc alloc = new Alloc();
    A a = new A();
    A b = a;
    b.b = new Object();
    b.b = alloc;
    Object query1 = b.b;
    QueryMethods.queryFor(query1);
  }

  @TestMethod
  public void test() {
    ObjectWithField a = new ObjectWithField();
    ObjectWithField b = a;
    Alloc file = new Alloc();
    bar(a, file);
    Object z = b.field;
    QueryMethods.queryFor(z);
  }

  private void bar(ObjectWithField a, Alloc file) {
    a.field = file;
  }

  @TestMethod
  public void test2() {
    ObjectWithField a = new ObjectWithField();
    ObjectWithField b = a;
    Alloc file = new Alloc();
    bar(a, b, file);
    QueryMethods.queryFor(b.field);
  }

  @TestMethod
  public void fieldStoreAndLoad2() {
    ObjectWithField container = new ObjectWithField();
    container.field = new Alloc();
    ObjectWithField otherContainer = new ObjectWithField();
    Object a = container.field;
    otherContainer.field = a;
    flowsToField(container);
    // mustBeInErrorState( container.field);
    QueryMethods.queryFor(a);
  }

  public static void flowsToField(ObjectWithField parameterContainer) {
    Object field = parameterContainer.field;
    Object aliasedVar = parameterContainer.field;
  }

  public void bar(ObjectWithField a, ObjectWithField b, Alloc file) {
    a.field = file;
  }

  public static class ObjectWithField {
    Object field = null;
  }

  public static class Alloc implements AllocatedObject {}

  private static class A1 {
    B1 b = new B1();
  }

  private static class B1 {
    Object c;
  }

  @TestMethod
  public void doubleNested() {
    A1 a = new A1();
    B1 x = a.b;
    x.c = new Object();
    B1 y = a.b;
    y.c = null;
    Object query = a.b.c;
    QueryMethods.queryFor(query);
  }

  @TestMethod
  public void doubleNestedBranched() {
    A1 a = new A1();
    if (Math.random() > 0.5) {
      a.b = new B1();
    } else {
      a.b = new B1();
    }
    a.b.c = new Object();

    B1 y = a.b;
    y.c = null;
    Object query = a.b.c;
    QueryMethods.queryFor(query);
  }
}
