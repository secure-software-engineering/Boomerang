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
package typestate;

import assertions.Assertions;
import java.util.Vector;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import test.IDEALTestRunnerInterceptor;
import test.TestConfig;
import test.TestParameters;
import typestate.impl.statemachines.VectorStateMachine;

@ExtendWith(IDEALTestRunnerInterceptor.class)
@TestConfig(
    stateMachine = VectorStateMachine.class,
    includedClasses = {java.util.Vector.class})
public class VectorTest {

  private boolean staticallyUnknown() {
    return Math.random() > 0.5;
  }

  @Test
  @TestParameters(expectedSeedCount = 1, expectedAssertionCount = 1)
  public void test1() {
    Vector<Object> s = new Vector<>();
    s.lastElement();
    Assertions.mustBeInErrorState(s);
  }

  @Test
  @TestParameters(expectedSeedCount = 1, expectedAssertionCount = 1)
  public void test2() {
    Vector<Object> s = new Vector<>();
    s.add(new Object());
    s.firstElement();
    Assertions.mustBeInAcceptingState(s);
  }

  @Test
  @TestParameters(expectedSeedCount = 1, expectedAssertionCount = 1)
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

  @Test
  @TestParameters(expectedSeedCount = 1, expectedAssertionCount = 3)
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

  @Test
  @TestParameters(expectedSeedCount = 1, expectedAssertionCount = 3)
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

  @Test
  @TestParameters(expectedSeedCount = 1, expectedAssertionCount = 1)
  public void test5() {
    Vector<Object> s = new Vector<>();
    s.add(new Object());
    if (staticallyUnknown()) s.firstElement();
    else s.elementAt(0);
    Assertions.mustBeInAcceptingState(s);
  }

  static Vector<Object> v;

  public static void foo() {}

  @Test
  @TestParameters(expectedSeedCount = 1, expectedAssertionCount = 1)
  public void staticAccessTest() {
    Vector<Object> x = new Vector<>();
    v = x;
    foo();
    v.firstElement();
    Assertions.mustBeInErrorState(v);
  }
}
