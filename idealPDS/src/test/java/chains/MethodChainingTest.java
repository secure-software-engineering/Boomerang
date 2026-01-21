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
package chains;

import assertions.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import test.IDEALTestRunnerInterceptor;
import test.TestConfig;
import test.TestParameters;

@ExtendWith(IDEALTestRunnerInterceptor.class)
@TestConfig(
    stateMachine = ChainStateMachine.class,
    excludedClasses = {Chain.class},
    flowFunctions = TestConfig.FlowFunctions.CHAINING)
public class MethodChainingTest {

  @Test
  @TestParameters(expectedSeedCount = 1, expectedAssertionCount = 1)
  public void noChainingTest1() {
    Chain chain = new Chain();
    chain.chain1();

    Assertions.mustBeInErrorState(chain);
  }

  @Test
  @TestParameters(expectedSeedCount = 1, expectedAssertionCount = 1)
  public void chainingTest1() {
    Chain chain = new Chain();
    chain.chain1();

    Assertions.mustBeInErrorState(chain);
  }

  @Test
  @TestParameters(expectedSeedCount = 1, expectedAssertionCount = 1)
  public void noChainingTest2() {
    Chain chain = new Chain();
    chain.chain1();
    chain.chain2();

    Assertions.mustBeInAcceptingState(chain);
  }

  @Test
  @TestParameters(expectedSeedCount = 1, expectedAssertionCount = 1)
  public void chainingTest2() {
    Chain chain = new Chain();
    chain.chain1().chain2();

    Assertions.mustBeInAcceptingState(chain);
  }

  @Test
  @TestParameters(expectedSeedCount = 1, expectedAssertionCount = 1)
  public void noChainingTest3() {
    Chain chain = new Chain();
    chain.chain1();
    chain.chain1();
    chain.chain2();

    Assertions.mustBeInAcceptingState(chain);
  }

  @Test
  @TestParameters(expectedSeedCount = 1, expectedAssertionCount = 1)
  public void chainingTest3() {
    Chain chain = new Chain();
    chain.chain1().chain1().chain2();

    Assertions.mustBeInAcceptingState(chain);
  }

  @Test
  @TestParameters(expectedSeedCount = 1, expectedAssertionCount = 1)
  public void noChainingTest4() {
    Chain chain = new Chain();
    chain.chain1();
    chain.chain1();
    chain.chain2();
    chain.chain1();

    Assertions.mustBeInErrorState(chain);
  }

  @Test
  @TestParameters(expectedSeedCount = 1, expectedAssertionCount = 1)
  public void chainingTest4() {
    Chain chain = new Chain();
    chain.chain1().chain1().chain2().chain1();

    Assertions.mustBeInErrorState(chain);
  }
}
