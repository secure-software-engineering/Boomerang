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

public class FieldClass {

  public int i;
  public A a;

  public static int si = 20;
  public static A sa = new A();

  public FieldClass() {
    i = 10;
    a = new A();
  }

  public A getA() {
    return a;
  }

  public void setA(A a) {
    this.a = a;
  }

  public static class InnerFieldClass {

    public int innerI;
    public A innerA;

    public InnerFieldClass() {
      innerI = 30;
      innerA = new A();
    }
  }
}
