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
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import test.ExpectedTestParameters;
import test.IDEalTestRunnerInterceptor;
import test.TestConfig;
import typestate.impl.statemachines.PrintWriterStateMachine;

@ExtendWith(IDEalTestRunnerInterceptor.class)
@TestConfig(
    stateMachine = PrintWriterStateMachine.class,
    includedClasses = {java.io.PrintWriter.class})
public class PrintWriterLongTest {

  @Test
  @ExpectedTestParameters(expectedSeedCount = 1, expectedAssertionCount = 1)
  public void test1() throws FileNotFoundException {
    PrintWriter inputStream = new PrintWriter("");
    inputStream.close();
    inputStream.flush();
    Assertions.mustBeInErrorState(inputStream);
  }
}
