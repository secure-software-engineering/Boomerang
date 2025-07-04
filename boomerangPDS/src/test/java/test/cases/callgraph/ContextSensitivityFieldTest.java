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
package test.cases.callgraph;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import test.core.BoomerangTestRunnerInterceptor;
import test.core.QueryMethods;

@ExtendWith(BoomerangTestRunnerInterceptor.class)
public class ContextSensitivityFieldTest {

  @Disabled("Method WrongSubclass.foo(Object o) is incorrectly marked as reachable.")
  @Test
  public void testOnlyCorrectContextInCallGraph() {
    wrongContext();
    SuperClass type = new CorrectSubclass();
    Object alloc = method(type);
    QueryMethods.queryFor(alloc);
  }

  public void wrongContext() {
    SuperClass type = new WrongSubclass();
    method(type);
  }

  public Object method(SuperClass type) {
    CallGraphAlloc alloc = new CallGraphAlloc();
    type.foo(alloc);
    return type.getO();
  }

  public static class SuperClass {
    Object o;

    public void foo(Object o) {
      this.o = o;
    }

    public Object getO() {
      return o;
    }
  }

  static class CorrectSubclass extends SuperClass {

    public void foo(Object o) {
      super.foo(o);
    }
  }

  static class WrongSubclass extends SuperClass {

    public void foo(Object o) {
      unreachable(o);
    }

    public void unreachable(Object o) {}
  }
}
