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
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import test.IDEalTestRunnerInterceptor;
import test.TestConfig;
import test.TestParameters;
import typestate.helper.File;
import typestate.impl.statemachines.FileMustBeClosedStateMachine;

@Disabled("Adapt call graph algorithms")
@ExtendWith(IDEalTestRunnerInterceptor.class)
@TestConfig(stateMachine = FileMustBeClosedStateMachine.class)
public class FileMustBeClosedDemandDrivenTest {

  @Test
  @TestParameters(expectedSeedCount = 1, expectedAssertionCount = 2)
  public void notCaughtByCHA() {
    I b = new B();
    callOnInterface(b);
  }

  private void callOnInterface(I i) {
    File file = new File();
    file.open();
    i.flow(file);
    Assertions.mustBeInAcceptingState(file);
  }

  @Test
  @TestParameters(expectedSeedCount = 1, expectedAssertionCount = 2)
  public void notCaughtByRTA() {
    I a = new A();
    I b = new B();
    callOnInterface(b);
  }

  private interface I {
    void flow(File f);
  }

  private static class B implements I {
    @Override
    public void flow(File f) {
      f.close();
    }
  }

  private static class A implements I {
    @Override
    public void flow(File f) {
      Assertions.shouldNotBeAnalyzed();
    }
  }
}
