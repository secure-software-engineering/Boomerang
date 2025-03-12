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
package test.cases.array;

import org.junit.Ignore;
import org.junit.Test;
import test.core.AbstractBoomerangTest;

public class ArrayTest extends AbstractBoomerangTest {

  private final String target = ArrayTarget.class.getName();

  @Test
  public void simpleAssignment() {
    analyze(target, testName.getMethodName());
  }

  @Test
  public void indexInsensitive() {
    analyze(target, testName.getMethodName());
  }

  @Test
  public void doubleArray() {
    analyze(target, testName.getMethodName());
  }

  @Test
  public void doubleArray3Address() {
    analyze(target, testName.getMethodName());
  }

  @Ignore
  @Test
  public void threeDimensionalArray() {
    analyze(target, testName.getMethodName());
  }

  @Test
  public void arrayCopyTest() {
    analyze(target, testName.getMethodName());
  }

  @Test
  public void arrayWithTwoObjectAndFieldTest() {
    analyze(target, testName.getMethodName());
  }

  @Test
  public void toCharArrayTest() {
    analyze(target, testName.getMethodName());
  }
}
