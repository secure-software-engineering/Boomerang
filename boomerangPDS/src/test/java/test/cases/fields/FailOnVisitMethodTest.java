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

// TODO The test does nothing?
public class FailOnVisitMethodTest extends AbstractBoomerangTest {

  private final String target = FailOnVisitMethodTarget.class.getName();

  @Test
  public void failOnVisitBar() {
    analyze(target, testName.getMethodName());
  }

  @Test
  public void failOnVisitBarSameMethod() {
    analyze(target, testName.getMethodName());
  }

  @Test
  public void failOnVisitBarSameMethodAlloc() {
    analyze(target, testName.getMethodName());
  }

  @Test
  public void failOnVisitBarSameMethodSimpleAlloc() {
    analyze(target, testName.getMethodName());
  }

  @Test
  public void doNotVisitBar() {
    analyze(target, testName.getMethodName());
  }
}
