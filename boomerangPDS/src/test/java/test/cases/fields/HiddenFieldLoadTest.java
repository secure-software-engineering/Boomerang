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
package test.cases.fields;

import org.junit.Test;
import test.core.AbstractBoomerangTest;

public class HiddenFieldLoadTest extends AbstractBoomerangTest {

  private final String target = HiddenFieldLoadTarget.class.getName();

  @Test
  public void run() {
    analyze(target, testName.getMethodName());
  }

  @Test
  public void run1() {
    analyze(target, testName.getMethodName());
  }

  @Test
  public void run7() {
    analyze(target, testName.getMethodName());
  }

  @Test
  public void run3() {
    analyze(target, testName.getMethodName());
  }

  @Test
  public void run6() {
    analyze(target, testName.getMethodName());
  }

  @Test
  public void run2() {
    analyze(target, testName.getMethodName());
  }

  @Test
  public void run4() {
    analyze(target, testName.getMethodName());
  }
}
