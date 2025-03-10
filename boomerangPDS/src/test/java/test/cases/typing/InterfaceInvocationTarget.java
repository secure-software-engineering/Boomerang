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
package test.cases.typing;

import test.TestMethod;
import test.core.QueryMethods;
import test.core.selfrunning.AllocatedObject;

@SuppressWarnings("unused")
public class InterfaceInvocationTarget {

  @TestMethod
  public void invokesInterface() {
    B b = new B();
    wrappedFoo(b);
    A a1 = new A();
    A a2 = new A();
    A a = null;
    if (Math.random() > 0.5) {
      a = a1;
    } else {
      a = a2;
    }
    wrappedFoo(a);
    QueryMethods.queryFor(a);
  }

  private void wrappedFoo(I a) {
    foo(a);
  }

  private void foo(I a) {
    a.bar();
  }

  private interface I {
    void bar();
  }

  private static class A implements I, AllocatedObject {
    @Override
    public void bar() {}
  }

  private static class B implements I {

    @Override
    public void bar() {}
  }
}
