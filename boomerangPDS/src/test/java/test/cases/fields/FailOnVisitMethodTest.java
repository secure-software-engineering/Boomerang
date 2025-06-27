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
import test.core.selfrunning.AllocatedObject;
import test.core.selfrunning.NullableField;

// TODO The test does nothing?
@ExtendWith(BoomerangTestRunnerInterceptor.class)
public class FailOnVisitMethodTest {

  private class A {
    B b = new B();
    E e = new E();
  }

  private static class B implements AllocatedObject {}

  private static class E {
    public void bar() {}
  }

  @Test
  public void failOnVisitBar() {
    A a = new A();
    B alias = a.b;
    E e = a.e;
    e.bar();
    QueryMethods.queryFor(alias);
  }

  private static class C {
    B b = null;
    E e = null;
  }

  @Test
  public void failOnVisitBarSameMethod() {
    C a = new C();
    a.b = new B();
    B alias = a.b;
    E e = a.e;
    e.bar();
    QueryMethods.queryFor(alias);
  }

  @Test
  public void failOnVisitBarSameMethodAlloc() {
    C a = new C();
    a.b = new B();
    a.e = new E();
    B alias = a.b;
    E e = a.e;
    e.bar();
    QueryMethods.queryFor(alias);
  }

  @Test
  @TestParameters(ignoreAllocSites = true)
  public void failOnVisitBarSameMethodSimpleAlloc() {
    Simplified a = new Simplified();
    a.e = new E();
    N alias = a.b;
    E e = a.e;
    e.bar();
    QueryMethods.queryFor(alias);
  }

  private static class Simplified {
    E e = null;
    N b = null;
  }

  @Test
  @TestParameters(ignoreAllocSites = true)
  public void doNotVisitBar() {
    O a = new O();
    N alias = a.nullableField;
    QueryMethods.queryFor(alias);
  }

  private static class O {
    N nullableField = null;
    E e = null;

    private O() {
      e = new E();
      e.bar();
    }
  }

  private static class N implements NullableField {}
}
