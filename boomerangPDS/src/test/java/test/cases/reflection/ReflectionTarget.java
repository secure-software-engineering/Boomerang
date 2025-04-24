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

import test.TestMethod;
import test.core.QueryMethods;

@SuppressWarnings("unused")
public class ReflectionTarget {

  @TestMethod
  public void bypassClassForName() throws ClassNotFoundException {
    ReflectionAlloc query = new ReflectionAlloc();
    Class<?> cls = Class.forName(A.class.getName());
    QueryMethods.queryFor(query);
  }

  @TestMethod
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
