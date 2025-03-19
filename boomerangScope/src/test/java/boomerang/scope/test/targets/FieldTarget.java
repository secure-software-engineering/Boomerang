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

public class FieldTarget {

  public static void main(String[] args) {
    fieldLoad();
    fieldStore();

    staticFieldLoad();
    staticFieldStore();

    innerFieldLoad();
    innerFieldStore();
  }

  public static void fieldLoad() {
    FieldClass fieldClass = new FieldClass();
    int i = fieldClass.i;
    A a = fieldClass.a;

    System.out.println(i + " " + a);
  }

  public static void fieldStore() {
    FieldClass fieldClass = new FieldClass();
    fieldClass.i = 100;
    fieldClass.a = new A();

    System.out.println(fieldClass);
  }

  public static void staticFieldLoad() {
    int i = FieldClass.si;
    A a = FieldClass.sa;

    a.methodCall(i);
  }

  public static void staticFieldStore() {
    FieldClass.si = 200;
    FieldClass.sa = new A();
  }

  public static void innerFieldLoad() {
    FieldClass.InnerFieldClass inner = new FieldClass.InnerFieldClass();
    int i = inner.innerI;
    A a = inner.innerA;

    System.out.println(i + " " + a);
  }

  public static void innerFieldStore() {
    FieldClass.InnerFieldClass inner = new FieldClass.InnerFieldClass();
    inner.innerI = 300;
    inner.innerA = new A();

    System.out.println(inner);
  }
}
