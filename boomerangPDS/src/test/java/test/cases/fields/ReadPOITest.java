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
package test.cases.fields;

import org.junit.Test;
import test.core.AbstractBoomerangTest;

public class ReadPOITest extends AbstractBoomerangTest {

  private final String target = ReadPOITarget.class.getName();

  @Test
  public void indirectAllocationSite() {
    analyze(target, testName.getMethodName());
  }

  @Test
  public void indirectAllocationSiteTwoFields3Address() {
    analyze(target, testName.getMethodName());
  }

  @Test
  public void indirectAllocationSiteTwoFields3Address2() {
    analyze(target, testName.getMethodName());
  }

  @Test
  public void unbalancedField() {
    analyze(target, testName.getMethodName());
  }

  @Test
  public void loadTwice() {
    analyze(target, testName.getMethodName(), true);
  }

  @Test
  public void indirectAllocationSiteTwoFields() {
    analyze(target, testName.getMethodName());
  }

  @Test
  public void twoFieldsBranched() {
    analyze(target, testName.getMethodName());
  }

  @Test
  public void oneFieldBranched() {
    analyze(target, testName.getMethodName());
  }

  @Test
  public void overwriteFieldWithItself() {
    analyze(target, testName.getMethodName());
  }
}
