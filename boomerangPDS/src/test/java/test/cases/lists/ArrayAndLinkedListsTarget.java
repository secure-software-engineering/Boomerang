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
package test.cases.lists;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import test.TestMethod;
import test.core.QueryMethods;

@SuppressWarnings("unused")
public class ArrayAndLinkedListsTarget {

  @TestMethod
  public void addAndRetrieve() {
    List<Object> list1 = new LinkedList<>();
    Object o = new ListAlloc();
    add(list1, o);
    Object o2 = new Object();
    List<Object> list2 = new ArrayList<>();
    add(list2, o2);
    QueryMethods.queryFor(o);
  }

  private void add(List<Object> list1, Object o) {
    list1.add(o);
  }
}
