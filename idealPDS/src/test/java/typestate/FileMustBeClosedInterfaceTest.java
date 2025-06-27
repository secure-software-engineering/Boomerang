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
import test.ExpectedTestParameters;
import test.IDEalTestRunnerInterceptor;
import test.TestConfig;
import typestate.helper.File;
import typestate.impl.statemachines.FileMustBeClosedStateMachine;

@ExtendWith(IDEalTestRunnerInterceptor.class)
@TestConfig(stateMachine = FileMustBeClosedStateMachine.class)
public class FileMustBeClosedInterfaceTest {

  private boolean staticallyUnknown() {
    return Math.random() > 0.5;
  }

  @Test
  @ExpectedTestParameters(expectedSeedCount = 1, expectedAssertionCount = 2)
  public void mainTest() {
    File file = new File();
    Flow flow = (staticallyUnknown() ? new ImplFlow1() : new ImplFlow2());
    flow.flow(file);
    Assertions.mayBeInErrorState(file);
    Assertions.mayBeInAcceptingState(file);
  }

  @Test
  @ExpectedTestParameters(expectedSeedCount = 1, expectedAssertionCount = 4)
  public void otherTest() {
    File file = new File();
    if (staticallyUnknown()) {
      new ImplFlow1().flow(file);
      Assertions.mustBeInErrorState(file);
    } else {
      new ImplFlow2().flow(file);
      Assertions.mustBeInAcceptingState(file);
    }
    Assertions.mayBeInAcceptingState(file);
    Assertions.mayBeInErrorState(file);
  }

  public static class ImplFlow1 implements Flow {
    @Override
    public void flow(File file) {
      file.open();
    }
  }

  public static class ImplFlow2 implements Flow {

    @Override
    public void flow(File file) {
      file.close();
    }
  }

  private interface Flow {
    void flow(File file);
  }
}
