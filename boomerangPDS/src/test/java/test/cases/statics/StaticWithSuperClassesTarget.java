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
package test.cases.statics;

import test.TestMethod;
import test.core.QueryMethods;

@SuppressWarnings("unused")
public class StaticWithSuperClassesTarget {

  @TestMethod
  public void simple() {
    List list = new List();
    Object o = list.get();
    QueryMethods.queryForAndNotEmpty(o);
  }

  private static class List {

    private static final Object elementData = new StaticsAlloc();

    public Object get() {
      return elementData;
    }
  }

  @TestMethod
  public void superClass() {
    MyList list = new MyList();
    Object o = list.get();
    QueryMethods.queryForAndNotEmpty(o);
  }

  private static class MyList extends List {

    private static final Object elementData2 = new StaticsAlloc();

    public Object get() {
      return elementData2;
    }
  }
}
