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
package test.cases.statics;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import test.core.BoomerangTestRunnerInterceptor;
import test.core.QueryMethods;

@ExtendWith(BoomerangTestRunnerInterceptor.class)
public class StaticFieldFlowsTest {

  private static Object alloc;
  private static StaticsAlloc instance;
  private static StaticsAlloc i;

  @Test
  public void simple() {
    alloc = new StaticsAlloc();
    Object alias = alloc;
    QueryMethods.queryFor(alias);
  }

  @Test
  public void simple2() {
    alloc = new StaticsAlloc();
    Object sr = new Object();
    Object r = new String();
    QueryMethods.queryFor(alloc);
  }

  @Test
  public void withCallInbetween() {
    alloc = new StaticsAlloc();
    alloc.toString();
    foo();
    QueryMethods.queryFor(alloc);
  }

  private void foo() {}

  @Test
  public void singleton() {
    StaticsAlloc singleton = v();
    Object alias = singleton;
    QueryMethods.queryFor(alias);
  }

  @Disabled
  @Test
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

  @Test
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

  @Test
  public void overwriteStatic() {
    alloc = new Object();
    alloc = new StaticsAlloc();
    Object alias = alloc;
    QueryMethods.queryFor(alias);
  }

  @Test
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

  @Test
  public void intraprocedural() {
    setStaticField();
    Object alias = alloc;
    QueryMethods.queryFor(alias);
  }

  private void setStaticField() {
    alloc = new StaticsAlloc();
  }
}
