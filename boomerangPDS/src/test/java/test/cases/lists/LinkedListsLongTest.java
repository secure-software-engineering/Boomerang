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
package test.cases.lists;

import java.util.List;
import org.junit.Test;
import test.cases.callgraph.ContextSpecificListTypeTarget;
import test.core.AbstractBoomerangTest;

public class LinkedListsLongTest extends AbstractBoomerangTest {

  private final String target = LinkedListsLongTarget.class.getName();

  @Override
  protected List<String> getIncludedPackages() {
    return List.of(
        "java.util.List",
        "java.util.AbstractList",
        "java.util.AbstractSequentialList",
        "java.util.LinkedList",
        "java.util.LinkedList$ListItr",
        "java.util.LinkedList$Node",
        "java.util.Iterator");
  }

  @Override
  protected List<String> getExcludedPackages() {
    // This is required because CHA adds an edge to the WrongList class, too
    return List.of(ContextSpecificListTypeTarget.WrongList.class.getName());
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

  @Test
  public void addAndRetrieveByIndex3() {
    analyze(target, testName.getMethodName());
  }
}
