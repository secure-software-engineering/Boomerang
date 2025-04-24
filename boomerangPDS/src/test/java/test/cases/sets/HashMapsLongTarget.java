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

import java.util.HashMap;
import java.util.Map;
import test.TestMethod;
import test.core.QueryMethods;
import test.core.selfrunning.AllocatedObject;

@SuppressWarnings("unused")
public class HashMapsLongTarget {

  @TestMethod
  public void addAndRetrieve() {
    Map<Object, Object> set = new HashMap<>();
    Object key = new Object();
    AllocatedObject alias3 = new SetAlloc();
    set.put(key, alias3);
    Object alias2 = null;
    for (Object o : set.values()) alias2 = o;
    Object ir = alias2;
    Object query2 = ir;
    QueryMethods.queryFor(query2);
  }
}
