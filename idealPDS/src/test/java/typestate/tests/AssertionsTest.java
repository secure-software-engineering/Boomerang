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
package typestate.tests;

import org.junit.Test;
import test.IDEALTestingFramework;
import typestate.finiteautomata.TypeStateMachineWeightFunctions;
import typestate.impl.statemachines.FileMustBeClosedStateMachine;
import typestate.targets.AssertionsTesting;

public class AssertionsTest extends IDEALTestingFramework {

  private final String target = AssertionsTesting.class.getName();

  @Override
  protected TypeStateMachineWeightFunctions getStateMachine() {
    return new FileMustBeClosedStateMachine();
  }

  @Test
  public void positiveMustBeInAcceptingStateTest() {
    analyze(target, testName.getMethodName(), 1, 1);
  }

  @Test(expected = AssertionError.class)
  public void negativeMustBeInAcceptingStateTest() {
    analyze(target, testName.getMethodName(), 1, 1);
  }

  @Test
  public void positiveMustBeInErrorStateTest() {
    analyze(target, testName.getMethodName(), 1, 1);
  }

  @Test(expected = AssertionError.class)
  public void negativeMustBeInErrorStateTest() {
    analyze(target, testName.getMethodName(), 1, 1);
  }

  @Test
  public void positiveMayBeInAcceptingState() {
    analyze(target, testName.getMethodName(), 2, 1);
  }

  @Test(expected = AssertionError.class)
  public void negativeMayBeInAcceptingState() {
    analyze(target, testName.getMethodName(), 1, 1);
  }

  @Test
  public void positiveMayBeInErrorState() {
    analyze(target, testName.getMethodName(), 2, 1);
  }

  @Test(expected = AssertionError.class)
  public void negativeMayBeInErrorState() {
    analyze(target, testName.getMethodName(), 1, 1);
  }

  @Test
  public void positiveShouldNotBeAnalyzedTest() {
    analyze(target, testName.getMethodName(), 0, 1);
  }

  @Test(expected = AssertionError.class)
  public void negativeShouldNotBeAnalyzedTest() {
    analyze(target, testName.getMethodName(), 1, 1);
  }
}
