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
package test.cases.sets;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import test.core.BoomerangTestRunnerInterceptor;
import test.core.QueryMethods;

@ExtendWith(BoomerangTestRunnerInterceptor.class)
public class CustomMapTest {

  @Test
  public void storeAndLoad() {
    SetAlloc alloc = new SetAlloc();
    Map map = new Map();
    map.add(alloc);
    Object alias = map.get();
    QueryMethods.queryFor(alias);
  }

  @Test
  public void storeAndLoadSimple() {
    SetAlloc alloc = new SetAlloc();
    Map map = new Map();
    map.add(alloc);
    Object alias = map.m.content;
    QueryMethods.queryFor(alias);
  }

  @Test
  public void onlyInnerMapSimple() {
    SetAlloc alloc = new SetAlloc();
    InnerMap map = new InnerMap();
    map.innerAdd(alloc);
    Object alias = map.content;
    QueryMethods.queryFor(alias);
  }

  public static class Map {
    public InnerMap m = new InnerMap();

    public void add(Object o) {
      InnerMap map = this.m;
      map.innerAdd(o);
      InnerMap alias = this.m;
      Object retrieved = alias.content;
    }

    public Object get() {
      InnerMap map = this.m;
      return map.get();
    }
  }

  public static class InnerMap {
    public Object content = null;

    public void innerAdd(Object o) {
      content = o;
    }

    public Object get() {
      return content;
    }
  }

  @Test
  public void storeAndLoadSimpleNoInnerClasses() {
    SetAlloc alloc = new SetAlloc();
    MyMap map = new MyMap();
    map.add(alloc);
    MyInnerMap load = map.m;
    Object alias = load.content;
    QueryMethods.queryFor(alias);
  }
}
