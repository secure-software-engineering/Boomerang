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
package boomerang.guided.targets;

public class ValueOfTarget {

  public static void main(String... args) {
    int z = 3;
    int x = 1;
    foo(x, z);
  }

  private static void foo(int x, int z) {
    Integer.valueOf(z);
    Query.queryFor(x);
  }
}
