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
package test.cases.subclassing;

import test.TestMethod;
import test.core.QueryMethods;
import test.core.selfrunning.AllocatedObject;

@SuppressWarnings("unused")
public class InnerClass2Target {

  public void doThings(final Object name) {
    class MyInner {
      public void seeOuter() {
        QueryMethods.queryFor(name);
      }
    }
    MyInner inner = new MyInner();
    inner.seeOuter();
  }

  @TestMethod
  public void run() {
    Object alloc = new Allocation();
    String cmd = System.getProperty("");
    if (cmd != null) {
      alloc = new Allocation();
    }
    InnerClass2Target outer = new InnerClass2Target();
    outer.doThings(alloc);
  }

  private static class Allocation implements AllocatedObject {}
}
