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

public class ArrayTarget {

  public static void main(String[] args) {
    singleArrayLoad(new int[] {1, 2});
    singleArrayStore();

    multiArrayStore();
  }

  public static void singleArrayLoad(int[] arr) {
    int i = arr[1];

    System.out.println(i);
  }

  public static void singleArrayStore() {
    int[] arr = new int[2];
    arr[0] = 1;

    System.out.println(Arrays.toString(arr));
  }

  public static void multiArrayStore() {
    int[][] arr = new int[2][2];
    arr[0][0] = 1;

    System.out.println(Arrays.deepToString(arr));
  }
}
