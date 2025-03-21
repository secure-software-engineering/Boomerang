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

public class InvokeExprTarget {

  public static void main(String[] args) {
    constructorCall();
    instanceInvokeExpr();
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

  public static void alias() {
    int i = 10;
    A a = new A();

    if (Math.random() > 0.5) {
      i = 10000;
    }

    a.methodCall(i);
  }
}
