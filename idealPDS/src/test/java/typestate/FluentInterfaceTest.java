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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import test.IDEALTestRunnerInterceptor;
import test.TestConfig;
import test.TestParameters;
import typestate.helper.File;
import typestate.impl.statemachines.FileMustBeClosedStateMachine;

@ExtendWith(IDEALTestRunnerInterceptor.class)
@TestConfig(stateMachine = FileMustBeClosedStateMachine.class)
public class FluentInterfaceTest {

  @Test
  @TestParameters(expectedSeedCount = 1, expectedAssertionCount = 1)
  public void fluentOpen() {
    File file = new File();
    file = file.open();
    Assertions.mustBeInErrorState(file);
  }

  @Test
  @TestParameters(expectedSeedCount = 1, expectedAssertionCount = 2)
  public void fluentOpenAndClose() {
    File file = new File();
    file = file.open();
    Assertions.mustBeInErrorState(file);
    file = file.close();
    Assertions.mustBeInAcceptingState(file);
  }
}
