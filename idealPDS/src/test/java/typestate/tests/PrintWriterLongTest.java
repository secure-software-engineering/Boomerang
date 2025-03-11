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

import java.io.FileNotFoundException;
import java.util.List;
import org.junit.Test;
import test.IDEALTestingFramework;
import typestate.finiteautomata.TypeStateMachineWeightFunctions;
import typestate.impl.statemachines.PrintWriterStateMachine;
import typestate.targets.PrintWriterLong;

public class PrintWriterLongTest extends IDEALTestingFramework {

  private final String target = PrintWriterLong.class.getName();

  @Override
  protected List<String> getIncludedPackages() {
    return List.of("java.io.PrintWriter");
  }

  @Override
  protected TypeStateMachineWeightFunctions getStateMachine() {
    return new PrintWriterStateMachine();
  }

  @Test
  public void test1() throws FileNotFoundException {
    analyze(target, testName.getMethodName(), 1, 1);
  }
}
