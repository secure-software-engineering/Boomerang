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

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import test.core.BoomerangTestRunnerInterceptor;
import test.core.QueryMethods;

@Disabled(
    "Cannot be tested because exceptions are instantiated implicitly (i.e. no 'new Exception()'")
@ExtendWith(BoomerangTestRunnerInterceptor.class)
public class ExceptionTest {

  @Test
  public void compileTimeExceptionFlow() {
    try {
      throwException();
    } catch (MyException e) {
      ExceptionAlloc object = e.field;
      QueryMethods.queryFor(e);
    }
  }

  @Test
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
