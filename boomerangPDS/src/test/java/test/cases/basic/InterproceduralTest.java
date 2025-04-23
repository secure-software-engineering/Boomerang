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
package test.cases.basic;

import org.junit.Ignore;
import org.junit.Test;
import test.core.AbstractBoomerangTest;

public class InterproceduralTest extends AbstractBoomerangTest {

  private final String target = InterproceduralTarget.class.getName();

  @Test
  public void identityTest() {
    analyze(target, testName.getMethodName());
  }

  @Test
  public void simpleAnonymous() {
    analyze(target, testName.getMethodName());
  }

  @Test
  public void simpleNonAnonymous() {
    analyze(target, testName.getMethodName());
  }

  @Test
  public void identityTest1() {
    analyze(target, testName.getMethodName());
  }

  @Test
  public void summaryReuseTest1() {
    analyze(target, testName.getMethodName());
  }

  @Test
  @Ignore("Soot fails")
  public void failedCast() {
    analyze(target, testName.getMethodName());
  }

  @Test
  public void summaryReuseTest4() {
    analyze(target, testName.getMethodName());
  }

  @Test
  public void branchWithCall() {
    analyze(target, testName.getMethodName());
  }

  @Test
  public void summaryReuseTest2() {
    analyze(target, testName.getMethodName());
  }

  @Test
  public void summaryReuseTest3() {
    analyze(target, testName.getMethodName());
  }

  @Test
  public void interLoop() {
    analyze(target, testName.getMethodName());
  }

  @Test
  public void wrappedAllocationSite() {
    analyze(target, testName.getMethodName());
  }

  @Test
  public void branchedObjectCreation() {
    analyze(target, testName.getMethodName());
  }

  @Test
  public void unbalancedCreation() {
    analyze(target, testName.getMethodName());
  }

  @Test
  public void unbalancedCreationStatic() {
    analyze(target, testName.getMethodName());
  }

  @Test
  public void heavySummary() {
    analyze(target, testName.getMethodName());
  }

  @Test
  public void summaryTest() {
    analyze(target, testName.getMethodName());
  }

  @Test
  public void doubleNestedSummary() {
    analyze(target, testName.getMethodName());
  }
}
