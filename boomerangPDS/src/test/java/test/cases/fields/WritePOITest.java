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

public class WritePOITest extends AbstractBoomerangTest {

  private final String target = WritePOITarget.class.getName();

  @Test
  public void overwrite() {
    analyze(target, testName.getMethodName());
  }

  @Test
  public void indirectAllocationSite12() {
    analyze(target, testName.getMethodName());
  }

  @Test
  public void indirectAllocationSite() {
    analyze(target, testName.getMethodName());
  }

  @Test
  public void indirectAllocationSite1() {
    analyze(target, testName.getMethodName());
  }

  @Test
  public void indirectAllocationSite144() {
    analyze(target, testName.getMethodName());
  }

  @Test
  public void indirectAllocationSite2() {
    analyze(target, testName.getMethodName());
  }

  @Test
  public void indirectAllocationSite3() {
    analyze(target, testName.getMethodName());
  }

  @Test
  public void doubleIndirectAllocationSite() {
    analyze(target, testName.getMethodName());
  }

  @Test
  public void doubleIndirectAllocationSiteSIMPLE() {
    analyze(target, testName.getMethodName());
  }

  @Test
  public void simpleIndirectAllocationSite() {
    analyze(target, testName.getMethodName());
  }

  @Test
  public void doubleIndirectAllocationSiteMoreComplex() {
    analyze(target, testName.getMethodName());
  }

  @Test
  public void directAllocationSite() {
    analyze(target, testName.getMethodName());
  }

  @Test
  public void directAllocationSiteSimpler() {
    analyze(target, testName.getMethodName());
  }

  @Test
  public void loadTwice() {
    analyze(target, testName.getMethodName());
  }

  @Test
  public void overwriteTwice() {
    analyze(target, testName.getMethodName());
  }

  @Test
  public void overwriteWithinCall() {
    analyze(target, testName.getMethodName());
  }

  @Test
  public void overwriteTwiceStrongAliased() {
    analyze(target, testName.getMethodName());
  }

  @Test
  public void test() {
    analyze(target, testName.getMethodName());
  }

  @Test
  public void test2() {
    analyze(target, testName.getMethodName());
  }

  @Test
  public void fieldStoreAndLoad2() {
    analyze(target, testName.getMethodName());
  }

  @Test
  public void doubleNested() {
    analyze(target, testName.getMethodName(), true);
  }

  @Test
  public void doubleNestedBranched() {
    analyze(target, testName.getMethodName(), true);
  }
}
