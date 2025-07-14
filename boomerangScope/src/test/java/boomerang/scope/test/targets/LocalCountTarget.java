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

public class LocalCountTarget {

  public static void main(String[] args) {
    LocalCountTarget target = new LocalCountTarget();
    target.virtualLocalCount(10, new A());
    staticLocalCount(10, new A());
  }

  /**
   * Virtual method with (at least) 5 locals (2 parameter + 2 defined + 'this' local)
   *
   * @param i primitive parameter
   * @param a ref parameter
   */
  public void virtualLocalCount(int i, A a) {
    int i2 = 10;
    a.methodCall(i2);
    A b = new A();
    b.methodCall(i);
  }

  /**
   * Static method with (at least) 4 locals (2 parameter + 2 defined locals)
   *
   * @param i primitive parameter
   * @param a ref parameter
   */
  public static void staticLocalCount(int i, A a) {
    int i2 = 10;
    a.methodCall(i2);
    A b = new A();
    b.methodCall(i);
  }
}
