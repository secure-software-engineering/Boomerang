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
import java.util.List;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import test.core.BoomerangTestRunnerInterceptor;
import test.core.QueryMethods;
import test.core.TestConfig;
import test.core.selfrunning.AllocatedObject;

@Disabled("Didn't work before, doesn't now")
@ExtendWith(BoomerangTestRunnerInterceptor.class)
@TestConfig(
    includedClasses = {
      "java.util.List",
      "java.util.AbstractList",
      "java.util.AbstractCollection",
      "java.util.ArrayList",
      "java.util.ArrayList$Itr",
      "java.util.Arrays",
      "java.lang.System",
      "java.util.Iterator"
    })
public class ArrayListsTest {

  @Test
  public void addAndRetrieveWithIterator() {
    List<Object> set = new ArrayList<>();
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
    List<Object> list = new ArrayList<>();
    ListAlloc alias = new ListAlloc();
    list.add(alias);
    Object ir = list.get(0);
    Object query2 = ir;
    QueryMethods.queryFor(query2);
  }

  @Test
  public void addAndRetrieveByIndex2() {
    List<Object> list = new ArrayList<>();
    AllocatedObject alias = new AllocatedObject() {};
    list.add(alias);
    Object ir = list.get(1);
    Object query2 = ir;
    QueryMethods.queryFor(query2);
  }

  @Test
  public void addAndRetrieveByIndex3() {
    ArrayList<Object> list = new ArrayList<>();
    Object b = new Object();
    Object a = new ListAlloc();
    list.add(a);
    list.add(b);
    Object c = list.get(0);
    QueryMethods.queryFor(c);
  }
}
