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
public class ReuseOfSummaryTarget {

  @TestMethod
  public void summaryTest() {
    A a = new A();
    A b = new A();

    Object c = new FieldAlloc(); // o1
    foo(a, b, c);
    foo(a, a, c);

    /*
     * the test case extracts all allocated object of type Alloc and assumes these objects to flow
     * as argument to queryFor(var). In this example var and a.f point to o1
     */
    Object var = a.f;
    QueryMethods.queryFor(var);
  }

  private void foo(A c, A d, Object f) {
    d.f = f;
  }

  private static class A {
    Object f;

    public A() {}
  }
}
