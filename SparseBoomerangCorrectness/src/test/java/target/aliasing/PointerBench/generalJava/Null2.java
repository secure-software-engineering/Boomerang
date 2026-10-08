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
package target.aliasing.PointerBench.generalJava;

import target.aliasing.PointerBench.benchmark.internal.Benchmark;
import target.aliasing.PointerBench.benchmark.objects.A;
import target.aliasing.PointerBench.benchmark.objects.B;

/*
 * @testcase Null2
 * @version 1.0
 * @author Johannes Sp�th, Nguyen Quang Do Lisa (Secure Software Engineering Group, Fraunhofer Institute SIT)
 *
 * @description Implicit alias to null
 *
 */
public class Null2 {

  public static void main(String[] args) {

    // No allocation site
    A a = new A();
    A b = a;
    B x = b.h; // a.h is null
    B x_q1 = x;
    Benchmark.query(x_q1);
    //		Benchmark
    //				.test("x",
    //						"{NULLALLOC, mayAlias:[], notMayAlias:[b,a], mustAlias:[b,a], notMustAlias:[i]}");
  }
}
