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
package test.cases.sets;

import java.util.Map;
import java.util.TreeMap;
import test.TestMethod;
import test.cases.fields.Alloc;
import test.core.QueryMethods;

@SuppressWarnings("unused")
public class TreeMapLongTarget {

  @TestMethod
  public void addAndRetrieve() {
    Map<Integer, Object> set = new TreeMap<>();
    Alloc alias = new Alloc();
    set.put(1, alias);
    Object query2 = set.get(2);
    QueryMethods.queryFor(query2);
  }
}
