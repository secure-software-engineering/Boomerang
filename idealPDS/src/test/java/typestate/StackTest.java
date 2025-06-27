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
import java.util.ArrayList;
import java.util.Stack;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import test.IDEalTestRunnerInterceptor;
import test.TestConfig;
import test.TestParameters;
import typestate.impl.statemachines.VectorStateMachine;

@ExtendWith(IDEalTestRunnerInterceptor.class)
@TestConfig(
    stateMachine = VectorStateMachine.class,
    includedClasses = {java.util.Stack.class, java.util.Vector.class})
public class StackTest {

  private boolean staticallyUnknown() {
    return Math.random() > 0.5;
  }

  @Test
  @TestParameters(expectedSeedCount = 1, expectedAssertionCount = 2)
  public void test1() {
    Stack<Object> s = new Stack<>();
    if (staticallyUnknown()) s.peek();
    else {
      Stack<Object> r = s;
      r.pop();
      Assertions.mustBeInErrorState(r);
    }
    Assertions.mustBeInErrorState(s);
  }

  @Test
  @TestParameters(expectedSeedCount = 1, expectedAssertionCount = 2)
  public void test4simple() {
    Stack<Object> s = new Stack<>();
    s.peek();
    Assertions.mustBeInErrorState(s);
    s.pop();
    Assertions.mustBeInErrorState(s);
  }

  @Test
  @TestParameters(expectedSeedCount = 1, expectedAssertionCount = 1)
  public void test2() {
    Stack<Object> s = new Stack<>();
    s.add(new Object());
    if (staticallyUnknown()) s.peek();
    else s.pop();
    Assertions.mustBeInAcceptingState(s);
  }

  @Test
  @TestParameters(expectedSeedCount = 1, expectedAssertionCount = 1)
  public void test6() {
    ArrayList<Object> l = new ArrayList<>();
    Stack<Object> s = new Stack<>();
    if (staticallyUnknown()) {
      s.push(new Object());
    }
    if (staticallyUnknown()) {
      s.push(new Object());
    }
    if (!s.isEmpty()) {
      Object pop = s.pop();
      Assertions.mayBeInErrorState(s);
    }
  }

  @Test
  @TestParameters(expectedSeedCount = 1, expectedAssertionCount = 2)
  public void test3() {
    Stack<Object> s = new Stack<>();
    s.peek();
    Assertions.mustBeInErrorState(s);
    s.pop();
    Assertions.mustBeInErrorState(s);
  }

  @Test
  @TestParameters(expectedSeedCount = 1, expectedAssertionCount = 1)
  public void test5() {
    Stack<Object> s = new Stack<>();
    s.peek();
    Assertions.mustBeInErrorState(s);
  }

  @Test
  @TestParameters(expectedSeedCount = 1, expectedAssertionCount = 2)
  public void test4() {
    Stack<Object> s = new Stack<>();
    s.peek();
    s.pop();

    Stack<Object> c = new Stack<>();
    c.add(new Object());
    c.peek();
    c.pop();
    Assertions.mustBeInErrorState(s);
    Assertions.mustBeInAcceptingState(c);
  }

  @Disabled("Broken since refactoring scope")
  @Test
  @TestParameters(expectedSeedCount = 1, expectedAssertionCount = 1)
  public void testInNewObject() {
    ObjectWithStack oWithStack = new ObjectWithStack();
    oWithStack.pushStack(new Object());
    oWithStack.get();
    Assertions.mustBeInAcceptingState(oWithStack.stack);
  }

  private static class ObjectWithStack {
    Stack<Object> stack;

    public void pushStack(Object o) {
      if (this.stack == null) {
        this.stack = new Stack<>();
      }
      this.stack.push(o);
    }

    public Object get() {
      if (stack == null || stack.empty()) {
        return null;
      }
      Object peek = this.stack.peek();
      Assertions.mustBeInAcceptingState(this.stack);
      return peek;
    }
  }
}
