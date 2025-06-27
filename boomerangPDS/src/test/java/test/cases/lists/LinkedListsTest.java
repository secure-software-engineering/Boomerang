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

import java.util.LinkedList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import test.core.BoomerangTestRunnerInterceptor;
import test.core.QueryMethods;
import test.core.TestConfig;
import test.core.selfrunning.AllocatedObject;

@ExtendWith(BoomerangTestRunnerInterceptor.class)
@TestConfig(
    includedClasses = {
      "java.util.List",
      "java.util.AbstractList",
      "java.util.AbstractSequentialList",
      "java.util.LinkedList",
      "java.util.LinkedList$ListItr",
      "java.util.LinkedList$Node",
      "java.util.Iterator",
      "java.util.ListIterator"
    })
public class LinkedListsTest {

  @Test
  public void addAndRetrieveWithIterator() {
    List<Object> set = new LinkedList<>();
    AllocatedObject alias = new AllocatedObject() {};
    set.add(alias);
    Object alias2 = null;
    for (Object o : set) alias2 = o;
    Object ir = alias2;
    Object query2 = ir;
    QueryMethods.queryFor(query2);
  }

  @Test
  public void addAndRetrieveByIndex1() {
    List<Object> list = new LinkedList<>();
    AllocatedObject alias = new AllocatedObject() {};
    list.add(alias);
    Object ir = list.get(0);
    Object query2 = ir;
    QueryMethods.queryFor(query2);
  }

  @Test
  public void addAndRetrieveByIndex2() {
    List<Object> list = new LinkedList<>();
    AllocatedObject alias = new AllocatedObject() {};
    list.add(alias);
    Object ir = list.get(1);
    Object query2 = ir;
    QueryMethods.queryFor(query2);
  }

  @Test
  public void addAndRetrieveByIndex3() {
    LinkedList<Object> list = new LinkedList<>();
    Object b = new Object();
    Object a = new ListAlloc();
    list.add(a);
    list.add(b);
    Object c = list.get(0);
    QueryMethods.queryFor(c);
  }
}
