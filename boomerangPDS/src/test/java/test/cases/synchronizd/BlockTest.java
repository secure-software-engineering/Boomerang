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
package test.cases.synchronizd;

import org.junit.Test;
import test.core.AbstractBoomerangTest;

public class BlockTest extends AbstractBoomerangTest {

  private final String target = BlockTarget.class.getName();

  @Test
  public void block() {
    analyze(target, testName.getMethodName());
  }

  @Test
  public void block2() {
    analyze(target, testName.getMethodName());
  }
}
