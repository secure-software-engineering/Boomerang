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
package test.cases.context;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import test.core.BoomerangTestRunnerInterceptor;
import test.core.QueryMethods;

@ExtendWith(BoomerangTestRunnerInterceptor.class)
public class ContextTypesTest {

  @Test
  public void openContext() {
    ContextAlloc alloc = new ContextAlloc();
    call(alloc);
  }

  @Test
  public void twoOpenContexts() {
    ContextAlloc alloc = new ContextAlloc();
    call(alloc);
    ContextAlloc a = new ContextAlloc();
    call(a);
  }

  @Test
  public void twoOpenContextsSameObject() {
    ContextAlloc alloc = new ContextAlloc();
    call(alloc);
    call(alloc);
  }

  private void call(ContextAlloc p) {
    QueryMethods.queryFor(p);
  }

  @Test
  public void closingContext() {
    ContextAlloc alloc = close();
    QueryMethods.queryFor(alloc);
  }

  private ContextAlloc close() {
    return new ContextAlloc();
  }

  @Test
  public void noContext() {
    ContextAlloc alloc = new ContextAlloc();
    QueryMethods.queryFor(alloc);
  }

  @Test
  public void twoClosingContexts() {
    ContextAlloc alloc = wrappedClose();
    QueryMethods.queryFor(alloc);
  }

  private ContextAlloc wrappedClose() {
    return close();
  }

  @Test
  public void openContextWithField() {
    A a = new A();
    ContextAlloc alloc = new ContextAlloc();
    a.b = alloc;
    call(a);
  }

  private void call(A a) {
    Object t = a.b;
    QueryMethods.queryFor(t);
  }

  public static class A {
    Object b = null;
    Object c = null;
  }

  @Test
  public void threeStackedOpenContexts() {
    ContextAlloc alloc = new ContextAlloc();
    wrappedWrappedCall(alloc);
  }

  private void wrappedWrappedCall(ContextAlloc alloc) {
    wrappedCall(alloc);
  }

  private void wrappedCall(ContextAlloc alloc) {
    call(alloc);
  }

  @Test
  public void recursionOpenCallStack() {
    ContextAlloc start = new ContextAlloc();
    recursionStart(start);
  }

  private void recursionStart(ContextAlloc rec) {
    if (Math.random() > 0.5) recursionStart(rec);
    call(rec);
  }
}
