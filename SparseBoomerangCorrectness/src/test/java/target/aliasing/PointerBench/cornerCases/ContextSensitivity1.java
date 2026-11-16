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

/*
 * @testcase ContextSensitivity1
 *
 * @version 1.0
 *
 * @author Johannes Späth, Nguyen Quang Do Lisa (Secure Software Engineering Group, Fraunhofer
 * Institute SIT)
 *
 * @description Object sensitive alias from caller object (1-CS)
 */
public class ContextSensitivity1 {

  public ContextSensitivity1() {}

  public void callee(A a, A b) {
    A b_q1 = b;
    Benchmark.query(b_q1);
    //    Benchmark.test("b",
    //        "{allocId:1, mayAlias:[a,b], notMayAlias:[], mustAlias:[a,b], notMustAlias:[]},"
    //            + "{allocId:2, mayAlias:[a], notMayAlias:[b], mustAlias:[a], notMustAlias:[b]}");
  }

  public void test1() {

    A a1 = new A();
    A b1 = a1;
    callee(a1, b1);
  }

  public void test2() {
    A a2 = new A();

    A b2 = new A();
    callee(a2, b2);
  }

  public static void main(String[] args) {
    ContextSensitivity1 cs1 = new ContextSensitivity1();
    cs1.test1();
    cs1.test2();
  }
}
