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

public class AllocValTarget {

  public static void main(String[] args) {
    AllocValTarget allocValTarget = new AllocValTarget();
    allocValTarget.allocate(new A());
  }

  /**
   * Method that has a 'this' local, a parameter local and a return local. Boomerang wraps allocated
   * values into an AllocVal, so all three locals may reach the solvers as an AllocVal rather than
   * as the plain local.
   */
  public A allocate(A a) {
    A allocated = new A();
    return allocated;
  }
}
