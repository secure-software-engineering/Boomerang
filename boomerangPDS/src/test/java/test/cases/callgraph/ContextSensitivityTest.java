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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import test.TestingFramework;
import test.core.BoomerangTestRunnerInterceptor;
import test.core.QueryMethods;
import test.core.TestParameters;

@ExtendWith(BoomerangTestRunnerInterceptor.class)
public class ContextSensitivityTest {

  @Test
  @TestParameters(skipFramework = {TestingFramework.Framework.SOOT})
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
    return alloc;
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
