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

import java.util.HashSet;
import java.util.Map;
import java.util.TreeMap;
import test.TestMethod;
import test.cases.fields.Alloc;
import test.core.QueryMethods;

@SuppressWarnings("unused")
public class TreeMapMultipleInstancesTarget {

  @TestMethod
  public void addAndRetrieve() {
    Map<Integer, Object> set = new TreeMap<>();
    Alloc alias = new Alloc();
    set.put(1, alias);
    Object query2 = set.get(2);
    QueryMethods.queryFor(query2);
    otherMap();
    otherMap2();
    hashMap();
  }

  @TestMethod
  public void contextSensitive() {
    Map<Integer, Object> map = new TreeMap<>();
    Object alias = new Alloc();
    Object ret = addToMap(map, alias);

    Map<Integer, Object> map2 = new TreeMap<>();
    Object noAlias = new Object();
    Object ret2 = addToMap(map2, noAlias);
    System.out.println(ret2);
    QueryMethods.queryFor(ret);
  }

  private Object addToMap(Map<Integer, Object> map, Object alias) {
    map.put(1, alias);
    Object query2 = map.get(2);
    return query2;
  }

  private void hashMap() {
    HashSet<Object> map = new HashSet<>();
    map.add(new Object());
    map = new HashSet<>();
    map.add(new Object());
    map = new HashSet<>();
    map.add(new Object());
    map = new HashSet<>();
    map.add(new Object());
    map = new HashSet<>();
    map.add(new Object());
    map = new HashSet<>();
    map.add(new Object());
    map = new HashSet<>();
    map.add(new Object());
    map = new HashSet<>();
    map.add(new Object());
    map = new HashSet<>();
    map.add(new Object());
    map = new HashSet<>();
    map.add(new Object());
    map = new HashSet<>();
    map.add(new Object());
    map = new HashSet<>();
    map.add(new Object());
    map = new HashSet<>();
    map.add(new Object());
    map = new HashSet<>();
    map.add(new Object());
    map = new HashSet<>();
    map.add(new Object());
  }

  private void otherMap2() {
    Map<Integer, Object> set = new TreeMap<>();
    Object alias = new Object();
    set.put(1, alias);
    set.put(2, alias);
    set.get(3);
  }

  private void otherMap() {
    Map<Integer, Object> set = new TreeMap<>();
    Object alias = new Object();
    set.put(1, alias);
    set.put(2, alias);
    set.get(3);
  }
}
