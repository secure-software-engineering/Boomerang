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
import test.cases.fields.Alloc;
import test.core.QueryMethods;

@SuppressWarnings("unused")
public class UnbalancedInterproceduralTarget {

  @TestMethod
  public void unbalancedCreation() {
    Alloc alias1 = create();
    Alloc query = alias1;
    QueryMethods.queryFor(query);
  }

  @TestMethod
  public void doubleUnbalancedCreation() {
    Alloc alias1 = wrappedCreate();
    Alloc query = alias1;
    QueryMethods.queryFor(query);
  }

  private Alloc wrappedCreate() {
    return create();
  }

  private Alloc create() {
    return new Alloc();
  }
}
