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

public class SingleTarget {

  public static void main(String[] args) {
    getAndSetField();
    identityTest();
    branching();
    branching2();
    usage();
    whileLoop();
    tryCatch();
  }

  public static void identityTest() {
    A alias1 = new A();
    A alias2 = identity(alias1);
    System.out.println(alias2);
  }

  private static A identity(A param) {
    A mapped = param;
    return mapped;
  }

  private static void getAndSetField() {
    FieldClass c = new FieldClass();
    A a = new A();

    c.setA(a);
    A query = c.getA();

    System.out.println(query);
  }

  private static int branching() {
    return ((Math.random() > 0.5) ? 10 : (Math.random() > 0.5) ? 100 : 1000);
  }

  private static int branching2() {
    int i;
    if (Math.random() > 0.5) {
      i = 10;
    } else {
      i = 100;
    }

    return i;
  }

  private static void whileLoop() {
    String s = "s";
    while (Math.random() > 0.5) {
      System.out.println("Loop");
    }
  }

  private static void usage() {
    A a = new A();
    int i = 10;

    a.methodCall(i);
    a.methodCall(i);
  }

  private static void tryCatch() {
    try {
      usage();
    } catch (Exception e) {
      System.out.println(e);
    }
  }
}
