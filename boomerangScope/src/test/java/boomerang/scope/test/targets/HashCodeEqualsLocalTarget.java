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

public class HashCodeEqualsLocalTarget {

  public static void main(String[] args) {
    HashCodeEqualsLocalTarget hashCodeEqualsLocalTarget = new HashCodeEqualsLocalTarget();

    A a = new A();
    hashCodeEqualsLocalTarget.parameterCall(a, 10);
    hashCodeEqualsLocalTarget.definedCall();
  }

  /**
   * Method to test whether parameter locals and used locals are equal
   *
   * @param a parameter local with reference type
   * @param i parameter local with primitive type
   */
  public void parameterCall(A a, int i) {
    a.methodCall(i);
  }

  /** Method to test whether defined locals and used locals are equal */
  public void definedCall() {
    A a = new A();
    int i = 10;
    a.methodCall(i);
  }
}
