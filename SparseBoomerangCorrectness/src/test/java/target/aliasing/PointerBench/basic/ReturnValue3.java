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
import target.aliasing.PointerBench.benchmark.objects.B;

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
public class ReturnValue3 {

  public static A id(A x) {
    A y = new A();

    y.f = new B();
    return y;
  }

  public static void main(String[] args) {

    A a = new A();
    A b = id(a);
    B x = b.f;
    B y = a.f;
    B x_q1 = x;
    Benchmark.query(x_q1);
    //    Benchmark.test("x",
    //        "{allocId:1, mayAlias:[x], notMayAlias:[a,b,y], mustAlias:[x],
    // notMustAlias:[a,b,y]}");
  }
}
