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
package test.cases.lists;

import java.util.LinkedList;
import java.util.List;
import test.TestMethod;
import test.core.QueryMethods;
import test.core.selfrunning.AllocatedObject;

@SuppressWarnings("unused")
public class LinkedListsTypeLongTarget {

  @TestMethod
  public void addAndRetrieveWithIteratorWithTyping() {
    List<I> list2 = new LinkedList<>();
    B b = new B();
    list2.add(b);
    List<I> list1 = new LinkedList<>();
    A alias = new A();
    list1.add(alias);
    I alias2 = null;
    for (I o : list1) alias2 = o;
    I ir = alias2;
    I query2 = ir;
    query2.bar();
    QueryMethods.queryFor(query2);
  }

  private static class A implements I, AllocatedObject {

    @Override
    public void bar() {}
  }

  private static class B implements I {

    @Override
    public void bar() {}
  }

  private interface I {
    void bar();
  }
}
