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

/*
 * @testcase Loops1
 *
 * @version 1.0
 *
 * @author Johannes Späth, Nguyen Quang Do Lisa (Secure Software Engineering Group, Fraunhofer
 * Institute SIT)
 *
 * @description The analysis must support loop constructs. No allocation site in N
 */
public class Loops1 {

  public class N {
    public String value = "";
    public N next;

    public N() {
      next = null;
    }
  }

  private void test() {

    N node = new N();

    int i = 0;
    while (i < 10) {
      node = node.next;
      i++;
    }

    N o = node.next;
    N p = node.next.next;
    N q = node.next.next.next;
    N node_q1 = node;
    Benchmark.query(node_q1);
    //    Benchmark
    //        .test("node",
    //            "{allocId:1, mayAlias:[node], notMayAlias:[i,o,p,q], mustAlias:[node],
    // notMustAlias:[i,o,p,q]}");
  }

  public static void main(String[] args) {
    Loops1 l1 = new Loops1();
    l1.test();
  }
}
