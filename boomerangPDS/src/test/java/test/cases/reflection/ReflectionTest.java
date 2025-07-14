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
package test.cases.reflection;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import test.core.BoomerangTestRunnerInterceptor;
import test.core.QueryMethods;
import test.core.TestParameters;

@ExtendWith(BoomerangTestRunnerInterceptor.class)
public class ReflectionTest {

  @Test
  public void bypassClassForName() throws ClassNotFoundException {
    ReflectionAlloc query = new ReflectionAlloc();
    Class<?> cls = Class.forName(A.class.getName());
    QueryMethods.queryFor(query);
  }

  @Test
  @TestParameters(ignoreAllocSites = true)
  public void loadObject()
      throws ClassNotFoundException, InstantiationException, IllegalAccessException {
    Class<?> cls = Class.forName(A.class.getName());
    Object newInstance = cls.newInstance();
    A a = (A) newInstance;
    ReflectionAlloc query = a.field;
    QueryMethods.queryFor(query);
  }

  private static class A {
    ReflectionAlloc field = new ReflectionAlloc();
  }
}
