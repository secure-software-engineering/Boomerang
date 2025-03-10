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
package test.cases.context;

import test.TestMethod;
import test.cases.fields.Alloc;
import test.core.QueryMethods;

@SuppressWarnings("unused")
public class ContextTypesTarget {

  @TestMethod
  public void openContext() {
    Alloc alloc = new Alloc();
    call(alloc);
  }

  @TestMethod
  public void twoOpenContexts() {
    Alloc alloc = new Alloc();
    call(alloc);
    Alloc a = new Alloc();
    call(a);
  }

  @TestMethod
  public void twoOpenContextsSameObject() {
    Alloc alloc = new Alloc();
    call(alloc);
    call(alloc);
  }

  private void call(Alloc p) {
    QueryMethods.queryFor(p);
  }

  @TestMethod
  public void closingContext() {
    Alloc alloc = close();
    QueryMethods.queryFor(alloc);
  }

  private Alloc close() {
    return new Alloc();
  }

  @TestMethod
  public void noContext() {
    Alloc alloc = new Alloc();
    QueryMethods.queryFor(alloc);
  }

  @TestMethod
  public void twoClosingContexts() {
    Alloc alloc = wrappedClose();
    QueryMethods.queryFor(alloc);
  }

  private Alloc wrappedClose() {
    return close();
  }

  @TestMethod
  public void openContextWithField() {
    A a = new A();
    Alloc alloc = new Alloc();
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

  @TestMethod
  public void threeStackedOpenContexts() {
    Alloc alloc = new Alloc();
    wrappedWrappedCall(alloc);
  }

  private void wrappedWrappedCall(Alloc alloc) {
    wrappedCall(alloc);
  }

  private void wrappedCall(Alloc alloc) {
    call(alloc);
  }

  @TestMethod
  public void recursionOpenCallStack() {
    Alloc start = new Alloc();
    recursionStart(start);
  }

  private void recursionStart(Alloc rec) {
    if (Math.random() > 0.5) recursionStart(rec);
    call(rec);
  }
}
