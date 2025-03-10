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

import java.io.File;

public class ContextSensitiveAndLeftUnbalancedFieldTarget {

  public static void main(String... args) {
    MyObject myObject = new MyObject();

    context(myObject.field);
  }

  private static void context(String barParam) {
    String bar = doPassArgument(barParam);
    new File(bar);
  }

  private static String doPassArgument(String param) {
    return new String(param);
  }

  private static class MyObject {
    private String field = "bar";
  }
}
