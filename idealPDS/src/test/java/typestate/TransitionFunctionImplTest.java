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
}
