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
import test.cases.fields.Alloc;
import test.core.QueryMethods;

@SuppressWarnings("unused")
public class StaticFieldFlowsTarget {

  private static Object alloc;
  private static Alloc instance;
  private static Alloc i;

  @TestMethod
  public void simple() {
    alloc = new Alloc();
    Object alias = alloc;
    QueryMethods.queryFor(alias);
  }

  @TestMethod
  public void simple2() {
    alloc = new Alloc();
    Object sr = new Object();
    Object r = new String();
    QueryMethods.queryFor(alloc);
  }

  @TestMethod
  public void withCallInbetween() {
    alloc = new Alloc();
    alloc.toString();
    foo();
    QueryMethods.queryFor(alloc);
  }

  private void foo() {}

  @TestMethod
  public void singleton() {
    Alloc singleton = v();
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
    i = new Alloc();
  }

  @TestMethod
  public void doubleUnbalancedSingleton() {
    Alloc singleton = returns();
    Object alias = singleton;
    QueryMethods.queryFor(alias);
  }

  private static Alloc returns() {
    return v();
  }

  private static Alloc v() {
    if (instance == null) instance = new Alloc();
    Alloc loaded = instance;
    return loaded;
  }

  @TestMethod
  public void overwriteStatic() {
    alloc = new Object();
    alloc = new Alloc();
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
    alloc = new Alloc();
  }

  @TestMethod
  public void intraprocedural() {
    setStaticField();
    Object alias = alloc;
    QueryMethods.queryFor(alias);
  }

  private void setStaticField() {
    alloc = new Alloc();
  }
}
