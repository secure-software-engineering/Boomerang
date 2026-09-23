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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import boomerang.scope.Method;
import boomerang.scope.Statement;
import boomerang.utils.MethodWrapper;
import java.util.Collections;
import org.junit.jupiter.api.Test;
import test.TestingFramework;
import wpds.impl.Weight;

public class TransitionFunctionImplTest {

  @Test
  public void testCombineWithCommutative() {
    TestingFramework testingFramework = new TestingFramework();
    testingFramework.getFrameworkScope(
        new MethodWrapper(getClass().getName(), "testCombineWithCommutative"));
    Method method = testingFramework.getTestMethod();
    Statement first = method.getStatements().get(0);
    Statement second = method.getStatements().get(1);
    assertNotEquals(first, second);
    TransitionFunctionImpl firstTransitionFunction =
        new TransitionFunctionImpl(Collections.emptySet(), first);
    TransitionFunctionImpl secondTransitionFunction =
        new TransitionFunctionImpl(Collections.emptySet(), second);
    assertNotEquals(firstTransitionFunction, secondTransitionFunction);
    Weight firstSecondResult = firstTransitionFunction.combineWith(secondTransitionFunction);
    Weight secondFirstResult = secondTransitionFunction.combineWith(firstTransitionFunction);
    assertEquals(firstSecondResult, secondFirstResult);
    // ensure that the behavior of getChangeStatement() is as in the
    // non-commutative implementation (whether this behavior is "meaningful" is
    // debatable, though)
    assertEquals(
        secondTransitionFunction.getStateChangeStatement(),
        ((TransitionFunctionImpl) firstSecondResult).getStateChangeStatement());
    assertEquals(
        firstTransitionFunction.getStateChangeStatement(),
        ((TransitionFunctionImpl) secondFirstResult).getStateChangeStatement());
  }

  @Test
  public void testCombineWithIdempotentAndCommutativeForMoreStatements() {
    TestingFramework testingFramework = new TestingFramework();
    testingFramework.getFrameworkScope(
        new MethodWrapper(
            getClass().getName(), "testCombineWithIdempotentAndCommutativeForMoreStatements"));
    Method method = testingFramework.getTestMethod();
    TransitionFunctionImpl first =
        new TransitionFunctionImpl(Collections.emptySet(), method.getStatements().get(0));
    TransitionFunctionImpl second =
        new TransitionFunctionImpl(Collections.emptySet(), method.getStatements().get(1));
    TransitionFunctionImpl third =
        new TransitionFunctionImpl(Collections.emptySet(), method.getStatements().get(2));

    // combining a function with itself must not change it, and in particular must not turn it
    // into a combined function
    Weight firstFirst = first.combineWith(first);
    assertEquals(first, firstFirst);
    assertInstanceOf(TransitionFunctionImpl.class, firstFirst);
    assertNotEquals(CombinedTransitionFunctionImpl.class, firstFirst.getClass());

    Weight firstSecond = first.combineWith(second);
    assertInstanceOf(CombinedTransitionFunctionImpl.class, firstSecond);
    assertEquals(firstSecond, firstSecond.combineWith(first));
    // the other direction takes the shortcut for a function that already has all statements
    assertEquals(firstSecond, first.combineWith(firstSecond));
    assertEquals(firstSecond.hashCode(), first.combineWith(firstSecond).hashCode());
    assertEquals(firstSecond, firstSecond.combineWith(firstSecond));

    Weight left = firstSecond.combineWith(third);
    Weight right = third.combineWith(second.combineWith(first));
    assertEquals(left, right);
    assertEquals(left.hashCode(), right.hashCode());
    assertNotEquals(firstSecond, left);
  }
}
