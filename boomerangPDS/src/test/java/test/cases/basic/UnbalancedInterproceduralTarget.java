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
package test.cases.basic;

import test.TestMethod;
import test.core.QueryMethods;

@SuppressWarnings("unused")
public class UnbalancedInterproceduralTarget {

  @TestMethod
  public void unbalancedCreation() {
    BasicAlloc alias1 = create();
    BasicAlloc query = alias1;
    QueryMethods.queryFor(query);
  }

  @TestMethod
  public void doubleUnbalancedCreation() {
    BasicAlloc alias1 = wrappedCreate();
    BasicAlloc query = alias1;
    QueryMethods.queryFor(query);
  }

  private BasicAlloc wrappedCreate() {
    return create();
  }

  private BasicAlloc create() {
    return new BasicAlloc();
  }
}
