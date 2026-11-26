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
package target.aliasing.PointerBench.collections;

import java.util.HashMap;
import target.aliasing.PointerBench.benchmark.internal.Benchmark;
import target.aliasing.PointerBench.benchmark.objects.A;

/*
 * @testcase Map1
 *
 * @version 1.0
 *
 * @author Johannes Späth, Nguyen Quang Do Lisa (Secure Software Engineering Group, Fraunhofer
 * Institute SIT)
 *
 * @description HashMap
 */
public class Map1 {

  public static void main(String[] args) {

    HashMap<String, A> map = new HashMap<String, A>();
    A a = new A();

    A b = new A();
    map.put("first", a);
    map.put("second", b);
    A c = map.get("second");
    A c_q1 = c;
    Benchmark.query(c_q1);
    Benchmark.test(
        "c",
        "{allocId:1, mayAlias:[c,b], notMayAlias:[a,map], mustAlias:[c,b], notMustAlias:[a,map]}");
  }
}
