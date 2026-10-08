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
 * @testcase ObjectSensitivity1
 *
 * @version 1.0
 *
 * @author Johannes Späth, Nguyen Quang Do Lisa (Secure Software Engineering Group, Fraunhofer
 * Institute SIT)
 *
 * @description Object sensitive alias from caller object
 */
public class ObjectSensitivity1 {

  public static void main(String[] args) {

    B b1 = new B();

    B b2 = new B();

    A a1 = new A(b1);
    A a2 = new A(b2);

    B b3 = a1.getF();
    B b4 = a2.getF();
    B b4_q1 = b4;
    Benchmark.query(b4_q1);

    //    Benchmark
    //        .test(
    //            "b4",
    //            "{allocId:1, mayAlias:[b4,b2], notMayAlias:[a1,a2,b1,b3], mustAlias:[b4,b2],
    // notMustAlias:[a1,a2,b1,b3]}");
  }
}
