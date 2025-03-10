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

public class PingPongInterproceduralTarget {

  public static void main(String... args) {
    StringBuilder sb = new StringBuilder();
    final String result = doCreateFileName(sb);
    File file = new File(result);
  }

  private static String doCreateFileName(StringBuilder sb) {
    sb.append("hello");
    appendMe(sb, "world");
    return sb.toString();
  }

  private static void appendMe(StringBuilder sb, String world) {
    sb.append(world);
  }
}
