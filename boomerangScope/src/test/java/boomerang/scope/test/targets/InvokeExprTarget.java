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

public class InvokeExprTarget {

  public static void main(String[] args) {
    constructorCall();
    instanceInvokeExpr();
    staticInvokeExpr();
    alias();
  }

  public static void constructorCall() {
    A a = new A();
  }

  public static void instanceInvokeExpr() {
    int i = 10;
    A a = new A();

    a.methodCall(i);
  }

  public static void staticInvokeExpr() {
    int i = 20;
    A.staticMethodCall(10, i);
  }

  public static void alias() {
    int i = 10;
    A a = new A();

    if (Math.random() > 0.5) {
      i = 10000;
    }

    a.methodCall(i);
  }

  public static void alias2() {
    A alias2 = new A();
    if (Math.random() > 0.5) {
      Object alias1 = alias2;
      alias2 = new A();
    }

    System.out.println(alias2);
  }

  public void cast() {
    A alias1 = new Subclass();
    Subclass alias2 = (Subclass) alias1;
    System.out.println(alias2);
  }

  public static class Subclass extends A {}
}
