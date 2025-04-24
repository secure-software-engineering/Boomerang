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

import java.io.File;

public class BranchingTest {

  public static void main(String... args) {
    String x = new String(Math.random() > 0 ? "bar" : "foo");
    String bar = doPassArgument(x);
    new File(bar);
  }

  public static String doPassArgument(String param) {
    String x = new String(param);
    System.out.println(x);
    return x;
  }
}
