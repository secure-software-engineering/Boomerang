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
package target.aliasing.PointerBench.basic;

import target.aliasing.PointerBench.benchmark.internal.Benchmark;
import target.aliasing.PointerBench.benchmark.objects.A;

/*
 * @testcase Branching1
 *
 * @version 1.0
 *
 * @author Johannes Späth, Nguyen Quang Do Lisa (Secure Software Engineering Group, Fraunhofer
 * Institute SIT)
 *
 * @description Condition. a and b alias on one path, not on the other
 */
public class Branching1 {

  public static void main(String[] args) {
    int i = 0;

    A a = new A();

    A b = new A();

    if (i < 0) a = b;
    A a_q1 = a;
    Benchmark.query(a_q1);

    //    Benchmark.test("a",
    //        "{allocId:1, mayAlias:[a], notMayAlias:[i,b], mustAlias:[a], notMustAlias:[i,b]},"
    //            + "{allocId:2, mayAlias:[a,b], notMayAlias:[i], mustAlias:[a],
    // notMustAlias:[i,b]}");
  }
}
