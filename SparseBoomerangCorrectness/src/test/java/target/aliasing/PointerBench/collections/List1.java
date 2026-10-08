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

import java.util.ArrayList;
import target.aliasing.PointerBench.benchmark.internal.Benchmark;
import target.aliasing.PointerBench.benchmark.objects.A;

/*
 * @testcase List1
 *
 * @version 1.0
 *
 * @author Johannes Späth, Nguyen Quang Do Lisa (Secure Software Engineering Group, Fraunhofer
 * Institute SIT)
 *
 * @description ArrayList
 */
public class List1 {

  public static void main(String[] args) {

    ArrayList<A> list = new ArrayList<A>();
    A a = new A();

    A b = new A();
    list.add(a);
    list.add(b);
    A c = list.get(1);
    A b_q1 = b;
    Benchmark.query(b_q1);
    //    Benchmark
    //        .test("b",
    //            "{allocId:1, mayAlias:[c,b], notMayAlias:[a,list], mustAlias:[c,b],
    // notMustAlias:[a,list]}");
  }
}
