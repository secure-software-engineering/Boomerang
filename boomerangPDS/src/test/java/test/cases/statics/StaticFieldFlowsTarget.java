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
public class StaticFieldFlowsTarget {

  private static Object alloc;
  private static StaticsAlloc instance;
  private static StaticsAlloc i;

  @TestMethod
  public void simple() {
    alloc = new StaticsAlloc();
    Object alias = alloc;
    QueryMethods.queryFor(alias);
  }

  @TestMethod
  public void simple2() {
    alloc = new StaticsAlloc();
    Object sr = new Object();
    Object r = new String();
    QueryMethods.queryFor(alloc);
  }

  @TestMethod
  public void withCallInbetween() {
    alloc = new StaticsAlloc();
    alloc.toString();
    foo();
    QueryMethods.queryFor(alloc);
  }

  private void foo() {}

  @TestMethod
  public void singleton() {
    StaticsAlloc singleton = v();
    Object alias = singleton;
    QueryMethods.queryFor(alias);
  }

  @TestMethod
  public void getAndSet() {
    setStatic();
    Object alias = getStatic();
    QueryMethods.queryFor(alias);
  }

  private Object getStatic() {
    return i;
  }

  private void setStatic() {
    i = new StaticsAlloc();
  }

  @TestMethod
  public void doubleUnbalancedSingleton() {
    StaticsAlloc singleton = returns();
    Object alias = singleton;
    QueryMethods.queryFor(alias);
  }

  private static StaticsAlloc returns() {
    return v();
  }

  private static StaticsAlloc v() {
    if (instance == null) instance = new StaticsAlloc();
    StaticsAlloc loaded = instance;
    return loaded;
  }

  @TestMethod
  public void overwriteStatic() {
    alloc = new Object();
    alloc = new StaticsAlloc();
    Object alias = alloc;
    QueryMethods.queryFor(alias);
  }

  @TestMethod
  public void overwriteStaticInter() {
    alloc = new Object();
    update();
    irrelevantFlow();
    Object alias = alloc;
    QueryMethods.queryFor(alias);
  }

  private int irrelevantFlow() {
    int x = 1;
    x = 2;
    x = 3;
    x = 4;
    return x;
  }

  private void update() {
    alloc = new StaticsAlloc();
  }

  @TestMethod
  public void intraprocedural() {
    setStaticField();
    Object alias = alloc;
    QueryMethods.queryFor(alias);
  }

  private void setStaticField() {
    alloc = new StaticsAlloc();
  }
}
