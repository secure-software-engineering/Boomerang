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
package test.cases.integers;

import org.junit.Ignore;
import org.junit.Test;
import test.core.AbstractBoomerangTest;

@Ignore("Extend IntAndStringAllocationSite")
public class IntTest extends AbstractBoomerangTest {

  private final String target = IntTarget.class.getName();

  @Test
  public void simpleAssign() {
    analyze(target, testName.getMethodName());
  }

  @Test
  public void simpleAssignBranched() {
    analyze(target, testName.getMethodName());
  }

  @Test
  public void simpleIntraAssign() {
    analyze(target, testName.getMethodName());
  }

  @Test
  public void simpleInterAssign() {
    analyze(target, testName.getMethodName());
  }

  @Test
  public void returnDirect() {
    analyze(target, testName.getMethodName());
  }

  @Test
  public void returnInDirect() {
    analyze(target, testName.getMethodName());
  }

  @Test
  public void wrappedType() {
    analyze(target, testName.getMethodName());
  }

  @Test
  public void wrappedTypeBigInteger() {
    analyze(target, testName.getMethodName());
  }
}
