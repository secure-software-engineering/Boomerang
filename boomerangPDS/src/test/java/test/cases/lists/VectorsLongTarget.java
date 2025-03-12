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

import java.util.List;
import java.util.Vector;
import test.TestMethod;
import test.core.QueryMethods;
import test.core.selfrunning.AllocatedObject;

@SuppressWarnings("unused")
public class VectorsLongTarget {

  @TestMethod
  public void addAndRetrieveWithIterator() {
    List<Object> set = new Vector<>();
    AllocatedObject alias = new AllocatedObject() {};
    set.add(alias);
    Object alias2 = null;
    for (Object o : set) alias2 = o;
    Object ir = alias2;
    Object query2 = ir;
    QueryMethods.queryFor(query2);
  }

  @TestMethod
  public void addAndRetrieveByIndex1() {
    List<Object> list = new Vector<>();
    AllocatedObject alias = new AllocatedObject() {};
    list.add(alias);
    Object ir = list.get(0);
    Object query2 = ir;
    QueryMethods.queryFor(query2);
  }

  @TestMethod
  public void addAndRetrieveByIndex2() {
    List<Object> list = new Vector<>();
    AllocatedObject alias = new AllocatedObject() {};
    list.add(alias);
    Object ir = list.get(1);
    Object query2 = ir;
    QueryMethods.queryFor(query2);
  }
}
