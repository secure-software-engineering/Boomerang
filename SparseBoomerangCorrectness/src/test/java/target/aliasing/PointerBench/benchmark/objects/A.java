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
package target.aliasing.PointerBench.benchmark.objects;

public class A {

  // Object A with attributes of type B

  public int i = 5;

  public B f = new B();
  public B g = new B();
  public B h;

  public A() {}

  public A(B b) {
    this.f = b;
  }

  public B getF() {
    return f;
  }

  public B getH() {
    return h;
  }

  public B id(B b) {
    return b;
  }
}
