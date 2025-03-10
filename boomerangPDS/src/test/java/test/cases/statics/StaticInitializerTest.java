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
package test.cases.statics;

import org.junit.Ignore;
import org.junit.Test;
import test.core.AbstractBoomerangTest;

@Ignore("Static fields are not handled correctly (see TODO in WeightedBoomerang")
public class StaticInitializerTest extends AbstractBoomerangTest {

  private final String target = StaticInitializerTarget.class.getName();

  @Test
  public void doubleSingleton() {
    analyze(target, testName.getMethodName());
  }
}
