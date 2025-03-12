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
package test.cases.context;

import test.TestMethod;
import test.core.QueryMethods;
import test.core.selfrunning.AllocatedObject;

@SuppressWarnings("unused")
public class AliasViaParameterTarget {

  @TestMethod
  public void aliasViaParameter() {
    A a = new A();
    A b = a;
    setAndLoadFieldOnAlias(a, b);
    AllocatedObject query = a.field;
    QueryMethods.queryFor(query);
  }

  @TestMethod
  public void aliasViaParameterWrapped() {
    A a = new A();
    A b = a;
    passThrough(a, b);
    AllocatedObject query = a.field;
    QueryMethods.queryFor(query);
  }

  private void passThrough(A a, A b) {
    setAndLoadFieldOnAlias(a, b);
  }

  private void setAndLoadFieldOnAlias(A a, A b) {
    b.field = new AllocatedObject() {};
  }

  public static class A {
    AllocatedObject field;
  }
}
