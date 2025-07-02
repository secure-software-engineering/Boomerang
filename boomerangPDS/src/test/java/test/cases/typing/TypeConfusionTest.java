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
package test.cases.typing;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import test.core.BoomerangTestRunnerInterceptor;
import test.core.QueryMethods;
import test.core.selfrunning.AllocatedObject;

@ExtendWith(BoomerangTestRunnerInterceptor.class)
public class TypeConfusionTest {

  @Test
  public void invokesInterface() {
    B b = new B();
    A a1 = new A();
    Object o = b;
    A a = null;
    if (Math.random() > 0.5) {
      a = a1;
    } else {
      a = (A) o;
    }
    QueryMethods.queryFor(a);
  }

  private static class A implements AllocatedObject {}

  private static class B {}
}
