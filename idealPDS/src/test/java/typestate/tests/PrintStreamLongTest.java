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

import org.junit.Assume;
import org.junit.Test;
import test.IDEALTestingFramework;
import test.setup.OpalTestSetup;
import typestate.finiteautomata.TypeStateMachineWeightFunctions;
import typestate.impl.statemachines.PrintStreamStateMachine;
import typestate.targets.PrintStreamLong;

public class PrintStreamLongTest extends IDEALTestingFramework {

  private final String target = PrintStreamLong.class.getName();

  @Override
  protected TypeStateMachineWeightFunctions getStateMachine() {
    return new PrintStreamStateMachine();
  }

  @Test
  public void test1() {
    Assume.assumeFalse(testSetup instanceof OpalTestSetup);
    analyze(target, testName.getMethodName(), 1, 1);
  }

  @Test
  public void test() {
    Assume.assumeFalse(testSetup instanceof OpalTestSetup);
    analyze(target, testName.getMethodName(), 1, 1);
  }
}
