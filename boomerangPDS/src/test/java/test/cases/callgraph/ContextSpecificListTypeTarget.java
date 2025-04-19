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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import test.TestMethod;
import test.core.QueryMethods;

@SuppressWarnings("unused")
public class ContextSpecificListTypeTarget {

  public void wrongContext() {
    List<Object> list = new WrongList();
    method(list);
  }

  public Object method(List<Object> list) {
    CallGraphAlloc alloc = new CallGraphAlloc();
    list.add(alloc);
    return alloc;
  }

  @TestMethod
  public void testListType() {
    wrongContext();
    List<Object> list = new ArrayList<>();
    Object query = method(list);
    QueryMethods.queryFor(query);
  }

  public static class WrongList extends LinkedList<Object> {
    @Override
    public boolean add(Object e) {
      unreachable();
      return false;
    }

    public void unreachable() {}
  }
}
