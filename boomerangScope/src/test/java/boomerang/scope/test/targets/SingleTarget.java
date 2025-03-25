/**
 * ***************************************************************************** 
 * Copyright (c) 2025 Fraunhofer IEM, Paderborn, Germany. This program and the
 * accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at http://www.eclipse.org/legal/epl-2.0.
 *
 * <p>SPDX-License-Identifier: EPL-2.0
 *
 * <p>Contributors: Johannes Spaeth - initial API and implementation
 * *****************************************************************************
 */
package boomerang.scope.test.targets;

public class SingleTarget {

  public static void main(String[] args) {
    identityTest();
  }

  public static void identityTest() {
    A alias1 = new A();
    A alias2 = identity(alias1);
    System.out.println(alias2);
  }

  private static A identity(A param) {
    A mapped = param;
    return mapped;
  }
}
