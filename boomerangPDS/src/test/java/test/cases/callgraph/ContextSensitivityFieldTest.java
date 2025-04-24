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
package test.cases.callgraph;

import org.junit.Ignore;
import org.junit.Test;
import test.core.AbstractBoomerangTest;

public class ContextSensitivityFieldTest extends AbstractBoomerangTest {

  private final String target = ContextSensitivityFieldTarget.class.getName();

  // Method WrongSubclass.foo(Object o) is incorrectly marked as reachable.
  @Ignore
  @Test
  public void testOnlyCorrectContextInCallGraph() {
    analyze(target, testName.getMethodName());
  }
}
