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
package typestate.targets;

import assertions.Assertions;
import java.util.Vector;
import test.TestMethod;

@SuppressWarnings("unused")
public class VectorLong {

  private boolean staticallyUnknown() {
    return Math.random() > 0.5;
  }

  @TestMethod
  public void test1() {
    Vector<Object> s = new Vector<>();
    s.lastElement();
    Assertions.mustBeInErrorState(s);
  }

  @TestMethod
  public void test2() {
    Vector<Object> s = new Vector<>();
    s.add(new Object());
    s.firstElement();
    Assertions.mustBeInAcceptingState(s);
  }

  @TestMethod
  public void test3() {
    Vector<Object> v = new Vector<>();
    try {
      v.removeAllElements();
      v.firstElement();
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }
    Assertions.mayBeInErrorState(v);
  }

  @TestMethod
  public void test4() {
    Vector<Object> v = new Vector<>();
    v.add(new Object());
    try {
      v.firstElement();
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }
    Assertions.mustBeInAcceptingState(v);
    if (staticallyUnknown()) {
      v.removeAllElements();
      v.firstElement();
      Assertions.mustBeInErrorState(v);
    }
    Assertions.mayBeInErrorState(v);
  }

  @TestMethod
  public void test6() {
    Vector<Object> v = new Vector<>();
    v.add(new Object());
    Assertions.mustBeInAcceptingState(v);
    if (staticallyUnknown()) {
      v.removeAllElements();
      v.firstElement();
      Assertions.mustBeInErrorState(v);
    }
    Assertions.mayBeInErrorState(v);
  }

  @TestMethod
  public void test5() {
    Vector<Object> s = new Vector<>();
    s.add(new Object());
    if (staticallyUnknown()) s.firstElement();
    else s.elementAt(0);
    Assertions.mustBeInAcceptingState(s);
  }

  static Vector<Object> v;

  public static void foo() {}

  @TestMethod
  public void staticAccessTest() {
    Vector<Object> x = new Vector<>();
    v = x;
    foo();
    v.firstElement();
    Assertions.mustBeInErrorState(v);
  }
}
