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
package test.cases.array;

import org.junit.Ignore;
import org.junit.Test;
import test.core.AbstractBoomerangTest;

public class ArrayContainerTest extends AbstractBoomerangTest {

  private final String target = ArrayContainerTarget.class.getName();

  @Test
  public void insertAndGet() {
    analyze(target, testName.getMethodName());
  }

  @Test
  public void insertAndGetField() {
    analyze(target, testName.getMethodName());
  }

  @Ignore
  @Test
  public void insertAndGetDouble() {
    analyze(target, testName.getMethodName());
  }
}
