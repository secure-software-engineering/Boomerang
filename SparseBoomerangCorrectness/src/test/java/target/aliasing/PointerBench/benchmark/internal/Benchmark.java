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
package target.aliasing.PointerBench.benchmark.internal;

public class Benchmark {

  public static void alloc(int id) {}

  public static void test(String targetVariable, String results) {}

  /** Marks the query of the sparse correctness tests: the aliases of the argument are compared */
  public static void query(Object o) {}

  public static void use(Object o) {
    // A method to be used to avoid the compiler to prune the Object
  }
}
