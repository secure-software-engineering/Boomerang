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

import java.util.Arrays;

public class AssignmentTarget {

  public static void main(String[] args) {
    arrayAllocation();
    constantAssignment();
  }

  public static void arrayAllocation() {
    int[] arr = new int[] {1, 2};

    System.out.println(Arrays.toString(arr));
  }

  public static void constantAssignment() {
    int i = 10;
    long l = 1000;
    String s = "test";

    System.out.println(i + l + " " + s);
  }
}
