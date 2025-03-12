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

import java.util.List;
import org.junit.Test;
import test.IDEALTestingFramework;
import typestate.finiteautomata.TypeStateMachineWeightFunctions;
import typestate.impl.statemachines.FileMustBeClosedStateMachineCallToReturn;
import typestate.targets.SootSceneSetup;
import typestate.targets.helper.File;

public class SootSceneSetupTest extends IDEALTestingFramework {

  private final String target = SootSceneSetup.class.getName();

  @Override
  protected TypeStateMachineWeightFunctions getStateMachine() {
    return new FileMustBeClosedStateMachineCallToReturn();
  }

  @Override
  public List<String> getExcludedPackages() {
    return List.of(File.class.getName());
  }

  @Test
  public void simple() {
    analyze(target, testName.getMethodName(), 2, 1);
  }

  @Test
  public void aliasSimple() {
    analyze(target, testName.getMethodName(), 2, 1);
  }
}
