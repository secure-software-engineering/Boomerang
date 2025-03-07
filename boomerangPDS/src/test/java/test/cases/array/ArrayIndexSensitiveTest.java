/**
 * ***************************************************************************** Copyright (c) 2020
 * CodeShield GmbH, Paderborn, Germany. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * <p>SPDX-License-Identifier: EPL-2.0
 *
 * <p>Contributors: Johannes Spaeth - initial API and implementation
 * *****************************************************************************
 */
package test.cases.array;

import org.junit.Test;
import test.core.AbstractBoomerangTest;

public class ArrayIndexSensitiveTest extends AbstractBoomerangTest {

  private final String target = ArrayIndexSensitiveTarget.class.getName();

  @Test
  public void simpleAssignment() {
    analyze(target, testName.getMethodName());
  }

  @Test
  public void arrayIndexOverwrite() {
    analyze(target, testName.getMethodName());
  }

  @Test
  public void arrayIndexNoOverwrite() {
    analyze(target, testName.getMethodName());
  }

  @Test
  public void arrayLoadInLoop() {
    analyze(target, testName.getMethodName());
  }

  @Test
  public void arrayStoreInLoop() {
    analyze(target, testName.getMethodName());
  }

  @Test
  public void copyArray() {
    analyze(target, testName.getMethodName());
  }
}
