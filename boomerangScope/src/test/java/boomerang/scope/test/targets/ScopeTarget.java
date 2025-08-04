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

public class ScopeTarget {

  public static void main(String[] args) {
    int i = methodWithStatements();
    System.out.println(i);
  }

  public static int methodWithStatements() {
    A a = new A();
    // Invoke expression
    a.methodCall(10);

    FieldClass fieldClass = new FieldClass();
    // Field store + assignment
    fieldClass.i = 10;

    // Field load + return
    return fieldClass.i;
  }
}
