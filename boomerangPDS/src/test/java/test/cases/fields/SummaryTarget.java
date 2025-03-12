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

public class SummaryTarget {

  @TestMethod
  public void branchedSummaryReuse() {
    A x = new A();
    B query = null;
    if (Math.random() > 0.5) {
      x.f = new B();
      query = load(x);
    } else {
      x.f = new B();
      query = load(x);
    }
    QueryMethods.queryFor(query);
  }

  @TestMethod
  public void simpleNoReuse() {
    A x = new A();
    x.f = new B();
    B query = load(x);
    QueryMethods.queryFor(query);
  }

  private B load(A x) {
    return x.f;
  }

  private class A {
    B f;
  }

  private class B implements AllocatedObject {}
}
