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
package test.cases.fields.loops;

import org.junit.Test;
import test.core.AbstractBoomerangTest;

public class LoopsWithFieldsIntraTest extends AbstractBoomerangTest {

  private final String target = LoopsWithFieldsIntraTarget.class.getName();

  @Test
  public void oneFields() {
    analyze(target, testName.getMethodName());
  }

  @Test
  public void twoFields() {
    analyze(target, testName.getMethodName());
  }

  @Test
  public void twoFieldSimpleLoop() {
    analyze(target, testName.getMethodName());
  }

  @Test
  public void twoFieldSimpleLoopWithBranched() {
    analyze(target, testName.getMethodName());
  }

  @Test
  public void threeFields() {
    analyze(target, testName.getMethodName());
  }

  @Test
  public void oneFieldSimpleLoopSingle() {
    analyze(target, testName.getMethodName());
  }
}
