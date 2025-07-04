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
package test.cases.unbalanced;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import test.cases.fields.B;
import test.core.BoomerangTestRunnerInterceptor;
import test.core.QueryMethods;

@ExtendWith(BoomerangTestRunnerInterceptor.class)
public class UnbalancedScopesTest {

  private boolean staticallyUnknown() {
    return Math.random() > 0.5;
  }

  @Test
  public void closingContext() {
    Object object = create();
    QueryMethods.queryFor(object);
  }

  @Test
  public void openingContext() {
    Object object = create();
    Object y = object;
    inner(y);
  }

  @Test
  public void doubleClosingContext() {
    Object object = wrappedCreate();
    QueryMethods.queryFor(object);
  }

  @Test
  public void branchedReturn() {
    Object object = aOrB();
    QueryMethods.queryFor(object);
  }

  @Test
  public void summaryReuse() {
    Object object = createA();
    Object y = object;
    Object x = id(y);
    QueryMethods.queryFor(x);
  }

  private Object createA() {
    UnbalancedAlloc c = new UnbalancedAlloc();
    Object d = id(c);
    return d;
  }

  private Object id(Object c) {
    return c;
  }

  private Object aOrB() {
    if (staticallyUnknown()) {
      return new UnbalancedAlloc();
    }
    return new B();
  }

  public Object wrappedCreate() {
    if (staticallyUnknown()) return create();
    return wrappedCreate();
  }

  private void inner(Object inner) {
    Object x = inner;
    QueryMethods.queryFor(x);
  }

  private Object create() {
    return new UnbalancedAlloc();
  }
}
