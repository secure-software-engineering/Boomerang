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
 * @testcase ObjectSensitivity2
 *
 * @version 1.0
 *
 * @author Johannes Späth, Nguyen Quang Do Lisa (Secure Software Engineering Group, Fraunhofer
 * Institute SIT)
 *
 * @description Object sensitive alias from parameter object
 */
public class ObjectSensitivity2 {

  public static void main(String[] args) {

    B b1 = new B();

    B b2 = new B();

    A a = new A();

    B b3 = a.id(b1);
    B b4 = a.id(b2);
    B b4_q1 = b4;
    Benchmark.query(b4_q1);
    //    Benchmark
    //        .test("b4",
    //            "{allocId:1, mayAlias:[b4,b2], notMayAlias:[a,b1,b3], mustAlias:[b4,b2],
    // notMustAlias:[a,b1,b3]}");
  }
}
