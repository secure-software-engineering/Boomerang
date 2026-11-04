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

/*
 * @testcase OuterClass1
 *
 * @version 1.0
 *
 * @author Johannes Späth, Nguyen Quang Do Lisa (Secure Software Engineering Group, Fraunhofer
 * Institute SIT)
 *
 * @description Alias from method in inner class
 */
public class OuterClass1 {

  public OuterClass1() {}

  public class InnerClass {
    private A a;

    public InnerClass(A a) {
      this.a = a;
    }

    public void alias(A x) {
      this.a = x;
    }
  }

  private void test() {

    A a = new A();
    A b = new A();

    InnerClass i = new InnerClass(a);
    i.alias(b);
    A h = i.a;
    A h_q1 = h;
    Benchmark.query(h_q1);
    //    Benchmark.test("h",
    //        "{allocId:1, mayAlias:[b,h], notMayAlias:[i,a], mustAlias:[b,a], notMustAlias:[i]}");
  }

  private static void main(String[] args) {
    OuterClass1 oc1 = new OuterClass1();
    oc1.test();
  }
}
