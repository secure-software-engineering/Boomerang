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
package test.cases.context;

import org.junit.Test;
import test.core.AbstractBoomerangTest;

public class ContextTypesTest extends AbstractBoomerangTest {

  private final String target = ContextTypesTarget.class.getName();

  @Test
  public void openContext() {
    analyze(target, testName.getMethodName());
  }

  @Test
  public void twoOpenContexts() {
    analyze(target, testName.getMethodName());
  }

  @Test
  public void twoOpenContextsSameObject() {
    analyze(target, testName.getMethodName());
  }

  @Test
  public void closingContext() {
    analyze(target, testName.getMethodName());
  }

  @Test
  public void noContext() {
    analyze(target, testName.getMethodName());
  }

  @Test
  public void twoClosingContexts() {
    analyze(target, testName.getMethodName());
  }

  @Test
  public void openContextWithField() {
    analyze(target, testName.getMethodName());
  }

  @Test
  public void threeStackedOpenContexts() {
    analyze(target, testName.getMethodName());
  }

  @Test
  public void recursionOpenCallStack() {
    analyze(target, testName.getMethodName());
  }
}
