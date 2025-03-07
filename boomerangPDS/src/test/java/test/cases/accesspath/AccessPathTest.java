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
package test.cases.accesspath;

import org.junit.Ignore;
import org.junit.Test;
import test.core.AbstractBoomerangTest;

@Ignore
public class AccessPathTest extends AbstractBoomerangTest {

  private final String target = AccessPathTarget.class.getName();

  @Test
  public void getAllAliases() {
    analyze(target, testName.getMethodName());
  }

  @Test
  public void sameField() {
    analyze(target, testName.getMethodName());
  }

  @Test
  public void getAllAliasesBranched() {
    analyze(target, testName.getMethodName());
  }

  @Test
  public void getAllAliasesLooped() {
    analyze(target, testName.getMethodName());
  }

  @Test
  public void getAllAliasesLoopedComplex() {
    analyze(target, testName.getMethodName());
  }

  @Test
  public void simpleIndirect() {
    analyze(target, testName.getMethodName());
  }

  @Test
  public void doubleIndirect() {
    analyze(target, testName.getMethodName());
  }

  @Test
  public void contextQuery() {
    analyze(target, testName.getMethodName());
  }

  @Test
  public void doubleContextQuery() {
    analyze(target, testName.getMethodName());
  }

  @Test
  public void twoLevelTest() {
    analyze(target, testName.getMethodName());
  }

  @Test
  public void threeLevelTest() {
    analyze(target, testName.getMethodName());
  }

  @Test
  public void hiddenFieldLoad() {
    analyze(target, testName.getMethodName());
  }

  @Test
  public void hiddenFieldLoad2() {
    analyze(target, testName.getMethodName());
  }

  @Test
  public void hiddenFieldLoad3() {
    analyze(target, testName.getMethodName());
  }

  @Test
  public void hiddenFieldLoad4() {
    analyze(target, testName.getMethodName());
  }
}
