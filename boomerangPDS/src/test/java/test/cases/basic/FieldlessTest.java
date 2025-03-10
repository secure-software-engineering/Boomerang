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
package test.cases.basic;

import org.junit.Test;
import test.core.AbstractBoomerangTest;

public class FieldlessTest extends AbstractBoomerangTest {

  private final String target = FieldlessTarget.class.getName();

  @Test
  public void simpleAssignment1() {
    analyze(target, testName.getMethodName());
  }

  @Test
  public void simpleAssignment2() {
    analyze(target, testName.getMethodName());
  }

  @Test
  public void branchWithOverwrite() {
    analyze(target, testName.getMethodName());
  }

  @Test
  public void branchWithOverwriteSwapped() {
    analyze(target, testName.getMethodName());
  }

  @Test
  public void returnNullAllocation() {
    analyze(target, testName.getMethodName());
  }

  @Test
  public void cast() {
    analyze(target, testName.getMethodName());
  }
}
