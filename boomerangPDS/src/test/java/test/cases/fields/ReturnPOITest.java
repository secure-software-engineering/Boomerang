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

public class ReturnPOITest extends AbstractBoomerangTest {

  private final String target = ReturnPOITarget.class.getName();

  @Test
  public void indirectAllocationSite() {
    analyze(target, testName.getMethodName());
  }

  @Test
  public void unbalancedReturnPOI1() {
    analyze(target, testName.getMethodName());
  }

  @Test
  public void unbalancedReturnPOI3() {
    analyze(target, testName.getMethodName());
  }

  @Test
  public void whyRecursiveReturnPOIIsNecessary() {
    analyze(target, testName.getMethodName());
  }

  @Test
  public void whysRecursiveReturnPOIIsNecessary() {
    analyze(target, testName.getMethodName());
  }

  @Test
  public void whysRecursiveReturnPOIIsNecessary3Addressed() {
    analyze(target, testName.getMethodName());
  }
}
