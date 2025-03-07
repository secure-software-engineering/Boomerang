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
package test.cases.threading;

import org.junit.Ignore;
import org.junit.Test;
import test.core.AbstractBoomerangTest;

public class InnerClassWithThreadTest extends AbstractBoomerangTest {

  private final String target = InnerClassWithThreadTarget.class.getName();

  @Ignore
  @Test
  public void runWithThreadStatic() {
    analyze(target, testName.getMethodName());
  }

  @Ignore
  @Test
  public void runWithThread() {
    analyze(target, testName.getMethodName());
  }

  @Test
  public void threadQuery() {
    analyze(target, testName.getMethodName());
  }
}
