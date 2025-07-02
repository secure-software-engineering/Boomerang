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

import static org.junit.jupiter.api.Assertions.assertThrows;

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
public class AssertionsTest {

  @Test
  @TestParameters(expectedSeedCount = 1, expectedAssertionCount = 1)
  public void positiveMustBeInAcceptingStateTest() {
    File file = new File();
    file.open();
    file.close();
    Assertions.mustBeInAcceptingState(file);
  }

  @Test
  @TestParameters(expectedSeedCount = 0, expectedAssertionCount = 0)
  public void negativeMustBeInAcceptingStateTest() {
    assertThrows(
        AssertionError.class,
        () -> {
          File file = new File();
          file.open();
          Assertions.mustBeInAcceptingState(file);
          file.close();
        });
  }

  @Test
  @TestParameters(expectedSeedCount = 1, expectedAssertionCount = 1)
  public void positiveMustBeInErrorStateTest() {
    File file = new File();
    file.open();
    Assertions.mustBeInErrorState(file);
    file.close();
  }

  @Test
  @TestParameters(expectedSeedCount = 0, expectedAssertionCount = 0)
  public void negativeMustBeInErrorStateTest() {
    assertThrows(
        AssertionError.class,
        () -> {
          File file = new File();
          file.open();
          file.close();
          Assertions.mustBeInErrorState(file);
        });
  }

  @Test
  @TestParameters(expectedSeedCount = 1, expectedAssertionCount = 2)
  public void positiveMayBeInAcceptingState() {
    File file = new File();
    file.open();
    if (Math.random() > 0.5) {
      file.close();
    }
    Assertions.mayBeInAcceptingState(file);
    Assertions.mayBeInErrorState(file);
  }

  @Test
  @TestParameters(expectedSeedCount = 0, expectedAssertionCount = 0)
  public void negativeMayBeInAcceptingState() {
    assertThrows(
        AssertionError.class,
        () -> {
          File file = new File();
          file.open();
          Assertions.mayBeInAcceptingState(file);
          file.close();
        });
  }

  @Test
  @TestParameters(expectedSeedCount = 1, expectedAssertionCount = 2)
  public void positiveMayBeInErrorState() {
    File file = new File();
    file.open();
    if (Math.random() > 0.5) {
      file.close();
    }
    Assertions.mayBeInErrorState(file);
    Assertions.mayBeInAcceptingState(file);
  }

  @Test
  @TestParameters(expectedSeedCount = 0, expectedAssertionCount = 0)
  public void negativeMayBeInErrorState() {
    assertThrows(
        AssertionError.class,
        () -> {
          File file = new File();
          file.open();
          file.close();
          Assertions.mayBeInErrorState(file);
        });
  }

  @Test
  @TestParameters(expectedSeedCount = 1, expectedAssertionCount = 0)
  public void positiveShouldNotBeAnalyzedTest() {
    File file = new File();
    // wrappedOpen(file);
    file.close();
  }

  @Test
  @TestParameters(expectedSeedCount = 0, expectedAssertionCount = 0)
  public void negativeShouldNotBeAnalyzedTest() {
    assertThrows(
        AssertionError.class,
        () -> {
          File file = new File();
          // Method should not be analyzed
          wrappedOpen(file);
          file.close();
        });
  }

  public void wrappedOpen(File file) {
    file.open();
    Assertions.shouldNotBeAnalyzed();
  }
}
