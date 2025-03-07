/**
 * ***************************************************************************** Copyright (c) 2018
 * Fraunhofer IEM, Paderborn, Germany. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * <p>SPDX-License-Identifier: EPL-2.0
 *
 * <p>Contributors: Johannes Spaeth - initial API and implementation
 * *****************************************************************************
 */
package test.cases.fields;

import org.junit.Test;
import test.core.AbstractBoomerangTest;

public class NoIndirectionTest extends AbstractBoomerangTest {

  private final String target = NoIndirectionTarget.class.getName();

  @Test
  public void doubleWriteAndReadFieldPositive() {
    analyze(target, testName.getMethodName());
  }

  @Test
  public void doubleWriteAndReadFieldNegative() {
    analyze(target, testName.getMethodName());
  }

  @Test
  public void writeWithinCallPositive() {
    analyze(target, testName.getMethodName());
  }

  @Test
  public void writeWithinCallNegative() {
    analyze(target, testName.getMethodName());
  }

  @Test
  public void writeWithinCallSummarizedPositive() {
    analyze(target, testName.getMethodName());
  }

  @Test
  public void doubleWriteWithinCallPositive() {
    analyze(target, testName.getMethodName());
  }

  @Test
  public void overwriteFieldTest() {
    analyze(target, testName.getMethodName());
  }

  @Test
  public void overwriteButPositiveFieldTest() {
    analyze(target, testName.getMethodName());
  }

  @Test
  public void overwriteButPositiveFieldTest2() {
    analyze(target, testName.getMethodName());
  }
}
