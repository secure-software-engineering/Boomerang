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

public class BranchingTarget {

  public static void main(String[] args) {
    switchBranching();
  }

  public static void switchBranching() {
    int i = (int) (Math.random() * 3);
    switch (i) {
      case 0:
        System.out.println(0);
        break;
      case 2:
        System.out.println(1);
        break;
      case 1:
        System.out.println(2);
        break;
      default:
        System.out.println(i);
    }
  }
}
