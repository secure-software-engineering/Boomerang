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
 * @testcase StaticVariables1
 *
 * @version 1.0
 *
 * @author Johannes Späth, Nguyen Quang Do Lisa (Secure Software Engineering Group, Fraunhofer
 * Institute SIT)
 *
 * @description Alias to a static variable, allocation site at the static variable site
 */
public class StaticVariables1 {

  private static A a;

  public static void main(String[] args) {

    a = new A();
    A b = a;
    A c = a;
    A b_q1 = b;
    Benchmark.query(b_q1);
    //    Benchmark.test("b",
    //        "{allocId:1, mayAlias:[b,c], notMayAlias:[], mustAlias:[b,c], notMustAlias:[]}");
  }
}
