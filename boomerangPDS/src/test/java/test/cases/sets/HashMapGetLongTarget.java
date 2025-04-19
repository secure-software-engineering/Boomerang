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

import java.util.HashMap;
import java.util.Map;
import test.TestMethod;
import test.core.QueryMethods;
import test.core.selfrunning.AllocatedObject;

@SuppressWarnings("unused")
public class HashMapGetLongTarget {

  @TestMethod
  public void addAndRetrieve() {
    Map<Object, Object> map = new HashMap<>();
    Object key = new Object();
    AllocatedObject alias3 = new SetAlloc();
    map.put(key, alias3);
    Object query = map.get(key);
    QueryMethods.queryFor(query);
  }

  @TestMethod
  public void addAndLoadFromOther() {
    Map<Object, Object> map = new HashMap<>();
    Object key = new Object();
    Object loadKey = new Object();
    AllocatedObject alias3 = new SetAlloc();
    map.put(key, alias3);
    Object query = map.get(loadKey);
    QueryMethods.queryFor(query);
  }
}
