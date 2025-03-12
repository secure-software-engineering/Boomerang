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
package test.cases.hashmap;

import org.junit.Test;
import test.core.AbstractBoomerangTest;

public class KeySensitiveTest extends AbstractBoomerangTest {

  private final String target = KeySensitiveTarget.class.getName();

  @Test
  public void directAccess() {
    analyze(target, testName.getMethodName());
  }

  @Test
  public void directAccess2Keys() {
    analyze(target, testName.getMethodName());
  }

  @Test
  public void overwrite() {
    analyze(target, testName.getMethodName());
  }

  @Test
  public void accessWithAliasedKey() {
    analyze(target, testName.getMethodName());
  }

  @Test
  public void accessWithKeyFromReturn() {
    analyze(target, testName.getMethodName());
  }

  @Test
  public void interprocedural() {
    analyze(target, testName.getMethodName());
  }
}
