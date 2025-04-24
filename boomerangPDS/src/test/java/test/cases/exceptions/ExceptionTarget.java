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
package test.cases.exceptions;

import test.TestMethod;
import test.core.QueryMethods;

@SuppressWarnings("unused")
public class ExceptionTarget {

  @TestMethod
  public void compileTimeExceptionFlow() {
    try {
      throwException();
    } catch (MyException e) {
      ExceptionAlloc object = e.field;
      QueryMethods.queryFor(e);
    }
  }

  @TestMethod
  public void runtimeExceptionFlow() {
    try {
      throwRuntimeException();
    } catch (MyRuntimeException e) {
      ExceptionAlloc object = e.field;
      QueryMethods.queryFor(e);
    }
  }

  private void throwRuntimeException() {
    new MyRuntimeException(new ExceptionAlloc());
  }

  private static class MyRuntimeException extends RuntimeException {
    ExceptionAlloc field;

    public MyRuntimeException(ExceptionAlloc alloc) {
      field = alloc;
    }
  }

  private void throwException() throws MyException {
    throw new MyException(new ExceptionAlloc());
  }

  private static class MyException extends Exception {
    ExceptionAlloc field;

    public MyException(ExceptionAlloc alloc) {
      field = alloc;
    }
  }
}
