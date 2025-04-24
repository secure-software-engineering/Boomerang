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

import org.junit.Test;
import test.IDEALTestingFramework;
import typestate.finiteautomata.TypeStateMachineWeightFunctions;
import typestate.impl.statemachines.FileMustBeClosedStateMachine;
import typestate.targets.FileMustBeClosedInterface;

public class FileMustBeClosedInterfaceTest extends IDEALTestingFramework {

  private final String target = FileMustBeClosedInterface.class.getName();

  @Override
  public TypeStateMachineWeightFunctions getStateMachine() {
    return new FileMustBeClosedStateMachine();
  }

  @Test
  public void mainTest() {
    analyze(target, testName.getMethodName(), 2, 1);
  }

  @Test
  public void otherTest() {
    analyze(target, testName.getMethodName(), 4, 1);
  }
}
