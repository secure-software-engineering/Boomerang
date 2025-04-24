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
package test.cases.exceptions;

import org.junit.Ignore;
import org.junit.Test;
import test.core.AbstractBoomerangTest;

@Ignore
public class ExceptionTest extends AbstractBoomerangTest {

  private final String target = ExceptionTarget.class.getName();

  @Test
  public void compileTimeExceptionFlow() {
    analyze(target, testName.getMethodName());
  }

  @Test
  public void runtimeExceptionFlow() {
    analyze(target, testName.getMethodName());
  }
}
