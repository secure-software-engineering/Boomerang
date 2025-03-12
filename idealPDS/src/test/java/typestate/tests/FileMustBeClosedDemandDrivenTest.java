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

import org.junit.Ignore;
import org.junit.Test;
import test.IDEALTestingFramework;
import typestate.finiteautomata.TypeStateMachineWeightFunctions;
import typestate.impl.statemachines.FileMustBeClosedStateMachine;
import typestate.targets.FileMustBeClosedDemandDriven;

@Ignore("Adapt call graph algorithms")
public class FileMustBeClosedDemandDrivenTest extends IDEALTestingFramework {

  private final String target = FileMustBeClosedDemandDriven.class.getName();

  @Override
  public TypeStateMachineWeightFunctions getStateMachine() {
    return new FileMustBeClosedStateMachine();
  }

  @Test
  public void notCaughtByCHA() {
    analyze(target, testName.getMethodName(), 2, 1);
  }

  @Test
  public void notCaughtByRTA() {
    analyze(target, testName.getMethodName(), 2, 1);
  }
}
