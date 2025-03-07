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
package test.cases.sets;

import java.util.List;
import org.junit.Test;
import test.core.AbstractBoomerangTest;

public class CustomSetTest extends AbstractBoomerangTest {

  private final String target = CustomSetTarget.class.getName();

  @Override
  protected List<String> getIncludedPackages() {
    return List.of("java.util.Iterator");
  }

  @Test
  public void mySetIteratorTest() {
    analyze(target, testName.getMethodName());
  }

  @Test
  public void mySetIterableTest() {
    analyze(target, testName.getMethodName());
  }
}
