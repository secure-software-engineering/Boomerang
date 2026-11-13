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
 * @testcase Method2
 *
 * @version 1.0
 *
 * @author Johannes Späth, Nguyen Quang Do Lisa (Secure Software Engineering Group, Fraunhofer
 * Institute SIT)
 *
 * @description Alias in a method
 */
public class Interprocedural2 {

  public Interprocedural2() {}

  public void alloc(A x, A y) {
    x.f = y.f;
  }

  public static void main(String[] args) {

    A a = new A();
    A b = new A();

    b.f = new B();
    Interprocedural2 m2 = new Interprocedural2();
    m2.alloc(a, b);

    B x = a.f;
    B y_q1 = b.f;
    Benchmark.query(y_q1);
    //    Benchmark
    //        .test("x",
    //            "{allocId:1, mayAlias:[x,y], notMayAlias:[a,b,m2], mustAlias:[x,y],
    // notMustAlias:[a,b,m2]}");
  }
}
