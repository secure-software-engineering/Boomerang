/**
 * ***************************************************************************** 
 * Copyright (c) 2018 Fraunhofer IEM, Paderborn, Germany
 * <p>
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * <p>
 * SPDX-License-Identifier: EPL-2.0
 * <p>
 * Contributors:
 *   Johannes Spaeth - initial API and implementation
 * *****************************************************************************
 */
package test.cases.basic;

import test.TestMethod;
import test.core.QueryMethods;
import test.core.selfrunning.AllocatedObject;

@SuppressWarnings("unused")
public class FieldlessTarget {

  @TestMethod
  public void simpleAssignment1() {
    Object alloc1 = new BasicAlloc();
    Object alias1 = alloc1;
    Object query = alias1;
    QueryMethods.queryFor(query);
  }

  @TestMethod
  public void simpleAssignment2() {
    Object alias1 = new AllocatedObject() {}, b, c, alias2, alias3;
    alias2 = alias1;
    c = new Object();
    alias3 = alias1;
    QueryMethods.queryFor(alias3);
  }

  @TestMethod
  public void branchWithOverwrite() {
    Object alias2 = new AllocatedObject() {};
    if (Math.random() > 0.5) {
      Object alias1 = alias2;
      alias2 = new BasicAlloc();
    }

    QueryMethods.queryFor(alias2);
  }

  @TestMethod
  public void branchWithOverwriteSwapped() {
    Object alias2 = new BasicAlloc();
    Object alias1 = new BasicAlloc();
    if (Math.random() > 0.5) {
      alias2 = alias1;
    }

    QueryMethods.queryFor(alias2);
  }

  @TestMethod
  public void returnNullAllocation() {
    Object alias2 = returnNull();
    QueryMethods.queryFor(alias2);
  }

  private Object returnNull() {
    Object x = new Object();
    return null;
  }

  @TestMethod
  public void cast() {
    BasicAlloc alias1 = new Subclass();
    Subclass alias2 = (Subclass) alias1;
    QueryMethods.queryFor(alias2);
  }

  public static class Subclass extends BasicAlloc {}

  public AllocatedObject create() {
    AllocatedObject alloc1 = new AllocatedObject() {};
    return alloc1;
  }
}
