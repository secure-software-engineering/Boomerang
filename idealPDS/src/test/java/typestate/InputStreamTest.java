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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import test.ExpectedTestParameters;
import test.IDEalTestRunnerInterceptor;
import test.TestConfig;
import typestate.impl.statemachines.InputStreamStateMachine;

@ExtendWith(IDEalTestRunnerInterceptor.class)
@TestConfig(
    stateMachine = InputStreamStateMachine.class,
    includedClasses = {java.io.FileInputStream.class})
public class InputStreamTest {

  @Test
  @ExpectedTestParameters(expectedSeedCount = 1, expectedAssertionCount = 1)
  public void test1() throws IOException {
    InputStream inputStream = new FileInputStream("");
    inputStream.close();
    inputStream.read(); // Go into error state
    Assertions.mustBeInErrorState(inputStream);
  }

  @Test
  @ExpectedTestParameters(expectedSeedCount = 2, expectedAssertionCount = 1)
  public void test2() throws IOException {
    InputStream inputStream = new FileInputStream("");
    inputStream.close();
    inputStream.close();
    inputStream.read(); // Go into error state
    Assertions.mustBeInErrorState(inputStream);
  }

  @Test
  @ExpectedTestParameters(expectedSeedCount = 1, expectedAssertionCount = 1)
  public void test3() throws IOException {
    InputStream inputStream = new FileInputStream("");
    inputStream.read();
    inputStream.close();
    Assertions.mustBeInAcceptingState(inputStream);
  }
}
