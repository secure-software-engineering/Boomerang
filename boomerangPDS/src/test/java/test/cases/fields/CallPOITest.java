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

public class CallPOITest extends AbstractBoomerangTest {

  private final String target = CallPOITarget.class.getName();

  @Test
  public void simpleButDiffer() {
    analyze(target, testName.getMethodName());
  }

  @Test
  public void indirectAllocationSite3Address() {
    analyze(target, testName.getMethodName());
  }

  @Test
  public void indirectAllocationSiteViaParameter() {
    analyze(target, testName.getMethodName());
  }

  @Test
  public void indirectAllocationSiteViaParameterAliased() {
    analyze(target, testName.getMethodName());
  }

  @Test
  public void whyRecursiveCallPOIIsNecessary() {
    analyze(target, testName.getMethodName());
  }

  @Test
  public void whyRecursiveCallPOIIsNecessarySimpler() {
    analyze(target, testName.getMethodName());
  }

  @Test
  public void whyRecursiveCallPOIIsNecessarySimpler2() {
    analyze(target, testName.getMethodName());
  }

  @Test
  public void innerSetFieldOnAlias() {
    analyze(target, testName.getMethodName());
  }

  @Test
  public void indirectAllocationSiteViaParameterAliasedNoPreAllocs() {
    analyze(target, testName.getMethodName());
  }

  @Test
  public void testForBackwardCallPOI() {
    // Thanks to Martin Mory for contributing the test
    analyze(target, testName.getMethodName());
  }
}
