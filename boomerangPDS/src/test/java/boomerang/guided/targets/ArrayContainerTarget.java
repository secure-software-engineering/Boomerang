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

public class ArrayContainerTarget {

  public static void main(String... args) {
    String[] y = new String[2];
    y[1] = hello();
    y[2] = world();
    y.toString();
  }

  public static String world() {
    return "world";
  }

  public static String hello() {
    return "hello";
  }
}
