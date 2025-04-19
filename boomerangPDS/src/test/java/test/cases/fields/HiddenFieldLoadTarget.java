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
    FieldAlloc alloc = new FieldAlloc();
    b.setF(alloc);
    // int x =1;
    Object alias = a.f();
    QueryMethods.queryFor(alias);
  }

  @TestMethod
  public void run6() {
    A b = new A();
    A a = b;
    FieldAlloc allocInRun6 = new FieldAlloc();
    b.setF(allocInRun6);
    int x = 1;
    Object alias = a.f;
    QueryMethods.queryFor(alias);
  }

  @TestMethod
  public void run2() {
    A b = new A();
    A a = b;
    FieldAlloc c = new FieldAlloc();
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
    b.f = new FieldAlloc();
    Object alias = a.f;
    QueryMethods.queryFor(alias);
  }

  private static class A {
    Object f;

    public void setF() {
      f = new FieldAlloc();
    }

    public void setFBranched() {
      if (staticallyUnknown()) {
        f = new FieldAlloc();
      } else {
        f = new FieldAlloc();
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
