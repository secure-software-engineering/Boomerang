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
package test.cases.context;

import test.TestMethod;
import test.core.QueryMethods;
import test.core.selfrunning.AllocatedObject;

@SuppressWarnings("unused")
public class SimpleContextQueryTarget {

  @TestMethod
  public void outerAllocation() {
    AllocatedObject alloc = new ContextAlloc();
    methodOfQuery(alloc);
  }

  private void methodOfQuery(AllocatedObject allocInner) {
    AllocatedObject alias = allocInner;
    QueryMethods.queryFor(alias);
  }

  @TestMethod
  public void outerAllocation2() {
    AllocatedObject alloc = new AllocatedObject() {};
    AllocatedObject same = alloc;
    methodOfQuery(alloc, same);
  }

  @TestMethod
  public void outerAllocation3() {
    AllocatedObject alloc = new AllocatedObject() {};
    Object same = new Object();
    methodOfQuery(alloc, same);
  }

  private void methodOfQuery(Object alloc, Object alias) {
    QueryMethods.queryFor(alloc);
  }
}
