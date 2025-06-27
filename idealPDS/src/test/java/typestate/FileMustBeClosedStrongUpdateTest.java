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
import typestate.helper.ObjectWithField;
import typestate.impl.statemachines.FileMustBeClosedStateMachine;

@ExtendWith(IDEalTestRunnerInterceptor.class)
@TestConfig(stateMachine = FileMustBeClosedStateMachine.class)
public class FileMustBeClosedStrongUpdateTest {

  private boolean staticallyUnknown() {
    return Math.random() > 0.5;
  }

  @Test
  @ExpectedTestParameters(expectedSeedCount = 2, expectedAssertionCount = 3)
  public void noStrongUpdatePossible() {
    File b = null;
    File a = new File();
    a.open();
    File e = new File();
    e.open();
    if (staticallyUnknown()) {
      b = a;
    } else {
      b = e;
    }
    b.close();
    Assertions.mayBeInErrorState(a);
    Assertions.mayBeInErrorState(e);
    Assertions.mustBeInAcceptingState(b);
  }

  @Test
  @ExpectedTestParameters(expectedSeedCount = 1, expectedAssertionCount = 2)
  public void aliasSensitive() {
    ObjectWithField a = new ObjectWithField();
    ObjectWithField b = a;
    File file = new File();
    file.open();
    a.field = file;
    File loadedFromAlias = b.field;
    loadedFromAlias.close();
    Assertions.mustBeInAcceptingState(file);
    Assertions.mustBeInAcceptingState(a.field);
  }
}
