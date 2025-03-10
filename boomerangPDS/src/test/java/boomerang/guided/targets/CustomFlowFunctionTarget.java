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
package boomerang.guided.targets;

public class CustomFlowFunctionTarget {

  public static void main(String... args) {
    int x = 1;
    int y = x + 1;
    Object z = new Object();
    System.exit(y);
    queryFor(z);
  }

  private static void queryFor(Object x) {}
}
