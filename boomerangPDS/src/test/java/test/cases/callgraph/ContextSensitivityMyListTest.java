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
package test.cases.callgraph;

import org.junit.Test;
import test.core.AbstractBoomerangTest;

public class ContextSensitivityMyListTest extends AbstractBoomerangTest {

  private final String target = ContextSensitivityMyListTarget.class.getName();

  @Test
  public void testOnlyCorrectContextInCallGraph() {
    analyze(target, testName.getMethodName());
  }
}
