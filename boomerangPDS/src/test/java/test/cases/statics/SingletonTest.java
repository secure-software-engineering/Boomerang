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
package test.cases.statics;

import org.junit.Ignore;
import org.junit.Test;
import test.core.AbstractBoomerangTest;

public class SingletonTest extends AbstractBoomerangTest {

  private final String target = SingletonTarget.class.getName();

  @Test
  @Ignore("Static fields are not handled correctly (see TODO in WeightedBoomerang")
  public void doubleSingleton() {
    analyze(target, testName.getMethodName());
  }

  @Test
  public void doubleSingletonDirect() {
    analyze(target, testName.getMethodName());
  }

  @Test
  @Ignore("Static fields are not handled correctly (see TODO in WeightedBoomerang")
  public void singletonDirect() {
    analyze(target, testName.getMethodName());
  }
}
