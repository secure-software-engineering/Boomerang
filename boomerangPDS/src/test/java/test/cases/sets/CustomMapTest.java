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
package test.cases.sets;

import org.junit.Test;
import test.core.AbstractBoomerangTest;

public class CustomMapTest extends AbstractBoomerangTest {

  private final String target = CustomMapTarget.class.getName();

  @Test
  public void storeAndLoad() {
    analyze(target, testName.getMethodName());
  }

  @Test
  public void storeAndLoadSimple() {
    analyze(target, testName.getMethodName());
  }

  @Test
  public void onlyInnerMapSimple() {
    analyze(target, testName.getMethodName());
  }

  @Test
  public void storeAndLoadSimpleNoInnerClasses() {
    analyze(target, testName.getMethodName());
  }
}
