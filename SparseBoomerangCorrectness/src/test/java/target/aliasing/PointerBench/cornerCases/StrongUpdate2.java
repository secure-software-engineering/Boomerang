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
package target.aliasing.PointerBench.cornerCases;

import target.aliasing.PointerBench.benchmark.internal.Benchmark;
import target.aliasing.PointerBench.benchmark.objects.A;
import target.aliasing.PointerBench.benchmark.objects.B;

/*
 * @testcase StrongUpdate1
 *
 * @version 1.0
 *
 * @author Johannes Späth, Nguyen Quang Do Lisa (Secure Software Engineering Group, Fraunhofer
 * Institute SIT)
 *
 * @description Indirect alias of a.f and b.f through alias of a and b
 */
public class StrongUpdate2 {

  public static void main(String[] args) {

    A a = new A();
    A b = a;
    B x = b.f;

    a.f = new B();
    B y = b.f;
    B y_q1 = y;
    Benchmark.query(y_q1);
    //    Benchmark.test("y",
    //        "{allocId:1, mayAlias:[y], notMayAlias:[x], mustAlias:[y], notMustAlias:[x]}");
  }
}
