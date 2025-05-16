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
package typestate.tests;

import java.util.List;
import org.junit.Test;
import test.IDEALTestingFramework;
import typestate.finiteautomata.TypeStateMachineWeightFunctions;
import typestate.impl.statemachines.HasNextStateMachine;
import typestate.targets.IteratorHasNext;

public class IteratorHasNextTest extends IDEALTestingFramework {

  private final String target = IteratorHasNext.class.getName();

  @Override
  protected List<String> getIncludedPackages() {
    return List.of("java.util.Iterator");
  }

  @Override
  protected TypeStateMachineWeightFunctions getStateMachine() {
    return new HasNextStateMachine();
  }

  @Test
  public void test1() {
    analyze(target, testName.getMethodName(), 1, 2);
  }

  @Test
  public void test2() {
    analyze(target, testName.getMethodName(), 1, 1);
  }

  @Test
  public void test3() {
    analyze(target, testName.getMethodName(), 1, 1);
  }

  @Test
  public void test4() {
    analyze(target, testName.getMethodName(), 1, 1);
  }

  @Test
  public void chartTest() {
    analyze(target, testName.getMethodName(), 4, 2);
  }
}
