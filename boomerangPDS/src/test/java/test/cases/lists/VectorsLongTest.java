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
package test.cases.lists;

import java.util.List;
import org.junit.Test;
import test.core.AbstractBoomerangTest;

public class VectorsLongTest extends AbstractBoomerangTest {

  private final String target = VectorsLongTarget.class.getName();

  @Override
  protected List<String> getIncludedPackages() {
    return List.of(
        "java.util.Vector",
        "java.util.Vector$Itr",
        "java.util.List",
        "java.util.Arrays",
        "java.util.Iterator");
  }

  @Test
  public void addAndRetrieveWithIterator() {
    analyze(target, testName.getMethodName());
  }

  @Test
  public void addAndRetrieveByIndex1() {
    analyze(target, testName.getMethodName());
  }

  @Test
  public void addAndRetrieveByIndex2() {
    analyze(target, testName.getMethodName());
  }
}
