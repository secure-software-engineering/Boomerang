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

public class NestedContextAndBranchingTarget {

  public static void main(String... args) {
    String bar = doPassArgument("bar");
    new File(bar);
  }

  private static String doPassArgument(String level0) {
    return wrappedWayDeeper(new String(level0));
  }

  private static String wrappedWayDeeper(String level1) {
    if (Math.random() > 0) {
      return "foo";
    }
    return andMoreStacks(level1);
  }

  private static String andMoreStacks(String level2) {
    return level2;
  }
}
