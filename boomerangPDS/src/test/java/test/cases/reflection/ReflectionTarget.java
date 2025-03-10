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
package test.cases.reflection;

import test.TestMethod;
import test.cases.fields.Alloc;
import test.core.QueryMethods;

@SuppressWarnings("unused")
public class ReflectionTarget {

  @TestMethod
  public void bypassClassForName() throws ClassNotFoundException {
    Alloc query = new Alloc();
    Class<?> cls = Class.forName(A.class.getName());
    QueryMethods.queryFor(query);
  }

  @TestMethod
  public void loadObject()
      throws ClassNotFoundException, InstantiationException, IllegalAccessException {
    Class<?> cls = Class.forName(A.class.getName());
    Object newInstance = cls.newInstance();
    A a = (A) newInstance;
    Alloc query = a.field;
    QueryMethods.queryFor(query);
  }

  private static class A {
    Alloc field = new Alloc();
  }
}
