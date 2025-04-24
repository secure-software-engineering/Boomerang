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
import test.core.selfrunning.NullableField;

@SuppressWarnings("unused")
public class FailOnVisitMethodTarget {

  private class A {
    B b = new B();
    E e = new E();
  }

  private static class B implements AllocatedObject {}

  private static class E {
    public void bar() {}
  }

  @TestMethod
  public void failOnVisitBar() {
    A a = new A();
    B alias = a.b;
    E e = a.e;
    e.bar();
    QueryMethods.queryFor(alias);
  }

  private static class C {
    B b = null;
    E e = null;
  }

  @TestMethod
  public void failOnVisitBarSameMethod() {
    C a = new C();
    a.b = new B();
    B alias = a.b;
    E e = a.e;
    e.bar();
    QueryMethods.queryFor(alias);
  }

  @TestMethod
  public void failOnVisitBarSameMethodAlloc() {
    C a = new C();
    a.b = new B();
    a.e = new E();
    B alias = a.b;
    E e = a.e;
    e.bar();
    QueryMethods.queryFor(alias);
  }

  @TestMethod
  public void failOnVisitBarSameMethodSimpleAlloc() {
    Simplified a = new Simplified();
    a.e = new E();
    N alias = a.b;
    E e = a.e;
    e.bar();
    QueryMethods.queryFor(alias);
  }

  private static class Simplified {
    E e = null;
    N b = null;
  }

  @TestMethod
  public void doNotVisitBar() {
    O a = new O();
    N alias = a.nullableField;
    QueryMethods.queryFor(alias);
  }

  private static class O {
    N nullableField = null;
    E e = null;

    private O() {
      e = new E();
      e.bar();
    }
  }

  private static class N implements NullableField {}
}
