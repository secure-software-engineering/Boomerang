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

public class ContextSensitiveAndLeftUnbalanced2StacksTarget {

  public static void main(String... args) {
    context();
  }

  private static void context() {
    String barParam = "bar";
    String bar = doPassArgument(barParam);
    String foo = doPassArgument("foo");
    String quz = doPassArgument("quz");
    new File(bar);
    new File(foo);
    new File(quz);
  }

  private static String doPassArgument(String paramDoPassArgument) {
    return wrapped(paramDoPassArgument);
  }

  private static String wrapped(String paramWrapped) {
    return new String(paramWrapped);
  }
}
