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
 * @testcase ContextSensitivity3
 *
 * @version 1.0
 *
 * @author Johannes Späth, Nguyen Quang Do Lisa (Secure Software Engineering Group, Fraunhofer
 * Institute SIT)
 *
 * @description Object sensitive alias from caller object (3-CS)
 */
public class ContextSensitivity3 {

  public ContextSensitivity3() {}

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
    test11(a1, b1);
  }

  private void test11(A a1, A b1) {
    test111(a1, b1);
  }

  private void test111(A a1, A b1) {
    callee(a1, b1);
  }

  public void test2() {
    A a2 = new A();

    A b2 = new A();
    test22(a2, b2);
  }

  private void test22(A a2, A b2) {
    test222(a2, b2);
  }

  private void test222(A a2, A b2) {
    callee(a2, b2);
  }

  public static void main(String[] args) {
    ContextSensitivity3 cs1 = new ContextSensitivity3();
    cs1.test1();
    cs1.test2();
  }
}
