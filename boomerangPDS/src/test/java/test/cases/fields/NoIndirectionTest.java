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
package test.cases.fields;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import test.core.BoomerangTestRunnerInterceptor;
import test.core.QueryMethods;
import test.core.TestParameters;

@ExtendWith(BoomerangTestRunnerInterceptor.class)
public class NoIndirectionTest {

  @Test
  public void doubleWriteAndReadFieldPositive() {
    Object query = new FieldAlloc();
    A a = new A();
    B b = new B();
    a.b = query;
    b.a = a;
    A c = b.a;
    Object alias = c.b;
    QueryMethods.queryFor(alias);
  }

  @Test
  @TestParameters(ignoreAllocSites = true)
  public void doubleWriteAndReadFieldNegative() {
    Object query = new Object();
    A a = new A();
    B b = new B();
    a.b = query;
    b.a = a;
    A c = b.a;
    Object alias = c.c;
    QueryMethods.unreachable(alias);
  }

  @Test
  public void writeWithinCallPositive() {
    FieldAlloc query = new FieldAlloc();
    A a = new A();
    call(a, query);
    Object alias = a.b;
    QueryMethods.queryFor(alias);
  }

  @Test
  @TestParameters(ignoreAllocSites = true)
  public void writeWithinCallNegative() {
    Object query = new Object();
    A a = new A();
    call(a, query);
    Object alias = a.c;
    QueryMethods.unreachable(alias);
  }

  @Test
  public void writeWithinCallSummarizedPositive() {
    FieldAlloc query = new FieldAlloc();
    A a = new A();
    call(a, query);
    Object alias = a.b;
    A b = new A();
    call(b, alias);
    Object summarizedAlias = b.b;
    QueryMethods.queryFor(summarizedAlias);
  }

  private void call(A a, Object query) {
    a.b = query;
  }

  @Test
  public void doubleWriteWithinCallPositive() {
    FieldAlloc query = new FieldAlloc();
    A a = new A();
    B b = callAndReturn(a, query);
    A first = b.a;
    Object alias = first.b;
    QueryMethods.queryFor(alias);
  }

  private B callAndReturn(A a, FieldAlloc query) {
    a.b = query;
    B b = new B();
    b.a = a;
    return b;
  }

  @Test
  @TestParameters(ignoreAllocSites = true)
  public void overwriteFieldTest() {
    Object query = new Object();
    A a = new A();
    a.b = query;
    a.b = null;
    Object alias = a.b;
    QueryMethods.unreachable(alias);
  }

  @Test
  public void overwriteButPositiveFieldTest() {
    FieldAlloc query = new FieldAlloc();
    A a = new A();
    a.b = query;
    // a.c = null;
    Object alias = a.b;
    QueryMethods.queryFor(alias);
  }

  @Test
  @TestParameters(ignoreAllocSites = true)
  public void overwriteButPositiveFieldTest2() {
    Object query = new Object();
    int x = 0;
    A a = new A();
    a.b = query;
    a.b = null;
    int y = x;
    Object alias = a.b;
    QueryMethods.queryFor(alias);
  }
}
