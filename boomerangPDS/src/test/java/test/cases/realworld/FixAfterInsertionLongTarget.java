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
package test.cases.realworld;

import test.TestMethod;
import test.core.QueryMethods;

@SuppressWarnings("unused")
public class FixAfterInsertionLongTarget {

  @TestMethod
  public void mainTest() {
    FixAfterInsertion.Entry<Object, Object> entry = new FixAfterInsertion.Entry<>(null, null, null);
    entry = new FixAfterInsertion.Entry<>(null, null, entry);
    new FixAfterInsertion<>().fixAfterInsertion(entry);
    FixAfterInsertion.Entry<Object, Object> query = entry.parent;
    QueryMethods.queryFor(query);
  }

  @TestMethod
  public void rotateLeftAndRightInLoop() {
    FixAfterInsertion.Entry<Object, Object> entry =
        new FixAfterInsertion.Entry<Object, Object>(null, null, null);
    entry = new FixAfterInsertion.Entry<>(null, null, entry);
    while (true) {
      new FixAfterInsertion<>().rotateLeft(entry);
      new FixAfterInsertion<>().rotateRight(entry);
      if (Math.random() > 0.5) break;
    }
    FixAfterInsertion.Entry<Object, Object> query = entry.parent;
    QueryMethods.queryFor(query);
  }
}
