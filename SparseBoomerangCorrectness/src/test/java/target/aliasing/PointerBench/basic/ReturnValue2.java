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
 * @testcase ReturnValue1
 *
 * @version 1.0
 *
 * @author Johannes Späth, Nguyen Quang Do Lisa (Secure Software Engineering Group, Fraunhofer
 * Institute SIT)
 *
 * @description Alias to a return value from a static method
 */
public class ReturnValue2 {

  public ReturnValue2() {}

  public A id(A x) {
    return x;
  }

  public static void main(String[] args) {

    A a = new A();
    ReturnValue2 rv2 = new ReturnValue2();
    A b = rv2.id(a);
    A b_q1 = b;
    Benchmark.query(b_q1);
    //    Benchmark.test("b",
    //        "{allocId:1, mayAlias:[a,b], notMayAlias:[rv2], mustAlias:[a,b],
    // notMustAlias:[rv2]}");
  }
}
