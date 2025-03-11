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
public class InterproceduralTarget {

  @TestMethod
  public void test3() {
    A a = new A();
    B b = new B();
    b.c = new C();
    alias(a, b);
    B h = a.b;
    C query = h.c;
    QueryMethods.queryFor(query);
  }

  private void alias(A a, B b) {
    a.b = b;
  }

  public static class A {
    B b;
  }

  public static class B {
    C c;
  }

  public static class C implements AllocatedObject {}
}
