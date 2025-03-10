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

public class ContextSensitiveTarget {

  public static void main(String... args) {
    String bar = doPassArgument("bar");
    String foo = doPassArgument("foo");
    String quz = doPassArgument("quz");
    new File(bar);
    new File(foo);
    new File(quz);
  }

  private static String doPassArgument(String param) {
    return new String(param);
  }
}
