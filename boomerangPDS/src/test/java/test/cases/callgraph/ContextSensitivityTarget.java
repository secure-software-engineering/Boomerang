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
package test.cases.callgraph;

import test.TestMethod;
import test.core.QueryMethods;

@SuppressWarnings("unused")
public class ContextSensitivityTarget {

  public void wrongContext() {
    SuperClass type = new WrongSubclass();
    method(type);
  }

  public Object method(SuperClass type) {
    CallGraphAlloc alloc = new CallGraphAlloc();
    type.foo(alloc);
    return alloc;
  }

  @TestMethod
  public void testOnlyCorrectContextInCallGraph() {
    wrongContext();
    SuperClass type = new CorrectSubclass();
    Object alloc = method(type);
    QueryMethods.queryFor(alloc);
  }

  public class SuperClass {

    public void foo(Object o) {
      QueryMethods.unreachable(o);
    }
  }

  class CorrectSubclass extends SuperClass {
    public void foo(Object o) {}
  }

  class WrongSubclass extends SuperClass {

    public void foo(Object o) {
      QueryMethods.unreachable(o);
    }
  }
}
