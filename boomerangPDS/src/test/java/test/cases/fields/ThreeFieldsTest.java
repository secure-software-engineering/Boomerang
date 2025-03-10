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

public class ThreeFieldsTest extends AbstractBoomerangTest {

  private final String target = ThreeFieldsTarget.class.getName();

  @Test
  public void indirectAllocationSite() {
    analyze(target, testName.getMethodName());
  }

  @Test
  public void indirectAllocationSite3Address() {
    analyze(target, testName.getMethodName());
  }

  @Test
  public void indirectAllocationSiteNoRead() {
    analyze(target, testName.getMethodName());
  }

  @Test
  public void indirectAllocationSiteNoReadOnlyTwoLevel() {
    analyze(target, testName.getMethodName());
  }

  @Test
  public void test() {
    analyze(target, testName.getMethodName());
  }
}
