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

public class LinkedListsTypeLongTest extends AbstractBoomerangTest {

  private final String target = LinkedListsTypeLongTarget.class.getName();

  @Override
  protected List<String> getIncludedPackages() {
    return List.of(
        "java.util.LinkedList",
        "java.util.LinkedList$ListItr",
        "java.util.LinkedList$Node",
        "java.util.AbstractList",
        "java.util.AbstractSequentialList",
        "java.util.List",
        "java.util.Iterator");
  }

  @Test
  public void addAndRetrieveWithIteratorWithTyping() {
    analyze(target, testName.getMethodName());
  }
}
