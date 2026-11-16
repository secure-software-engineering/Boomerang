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
package boomerang.scope.test.targets;

public class StartPointTarget {

  /** Its exception handler is an entry point of the graph, but not of the method. */
  public int methodWithHandler(int divisor) {
    try {
      return 10 / divisor;
    } catch (ArithmeticException e) {
      return -1;
    }
  }

  /** No identity statements: the first statement is the call. */
  public static int delegate() {
    return compute();
  }

  private static int compute() {
    return 42;
  }
}
