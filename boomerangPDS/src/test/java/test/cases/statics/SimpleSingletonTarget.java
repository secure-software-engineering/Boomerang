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
package test.cases.statics;

import test.TestMethod;
import test.core.QueryMethods;

@SuppressWarnings("unused")
public class SimpleSingletonTarget {

  @TestMethod
  public void singletonDirect() {
    StaticsAlloc singleton = alloc;
    QueryMethods.queryForAndNotEmpty(singleton);
  }

  private static StaticsAlloc alloc = new StaticsAlloc();

  @TestMethod
  public void staticInnerAccessDirect() {
    Runnable r =
        new Runnable() {

          @Override
          public void run() {
            StaticsAlloc singleton = alloc;
            QueryMethods.queryForAndNotEmpty(singleton);
          }
        };
    r.run();
  }

  @TestMethod
  public void simpleWithAssign() {
    alloc = new StaticsAlloc();
    Object b = alloc;
    QueryMethods.queryFor(b);
  }

  @TestMethod
  public void simpleWithAssign2() {
    alloc = new StaticsAlloc();
    Object b = alloc;
    Object a = alloc;
    QueryMethods.queryFor(b);
  }
}
