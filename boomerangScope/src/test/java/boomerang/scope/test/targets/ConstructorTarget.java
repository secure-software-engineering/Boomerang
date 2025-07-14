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
package boomerang.scope.test.targets;

public class ConstructorTarget {

  public static void main(String[] args) {
    definedConstructor();
    undefinedConstructor();
  }

  public static void definedConstructor() {
    ClassWithDefinedField defined = new ClassWithDefinedField();
    System.out.println(defined.a + " " + defined.i);
  }

  public static void undefinedConstructor() {
    ClassWithUndefinedField undefined = new ClassWithUndefinedField();
    System.out.println(undefined.a + " " + undefined.i);
  }

  private static class ClassWithDefinedField {
    A a = new A();
    int i;
  }

  private static class ClassWithUndefinedField {
    A a;
    int i = 10;
  }
}
