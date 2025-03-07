package test.cases.exceptions;

import test.TestMethod;
import test.cases.fields.Alloc;
import test.core.QueryMethods;

@SuppressWarnings("unused")
public class ExceptionTarget {

  @TestMethod
  public void compileTimeExceptionFlow() {
    try {
      throwException();
    } catch (MyException e) {
      Alloc object = e.field;
      QueryMethods.queryFor(e);
    }
  }

  @TestMethod
  public void runtimeExceptionFlow() {
    try {
      throwRuntimeException();
    } catch (MyRuntimeException e) {
      Alloc object = e.field;
      QueryMethods.queryFor(e);
    }
  }

  private void throwRuntimeException() {
    new MyRuntimeException(new Alloc());
  }

  private static class MyRuntimeException extends RuntimeException {
    Alloc field;

    public MyRuntimeException(Alloc alloc) {
      field = alloc;
    }
  }

  private void throwException() throws MyException {
    throw new MyException(new Alloc());
  }

  private static class MyException extends Exception {
    Alloc field;

    public MyException(Alloc alloc) {
      field = alloc;
    }
  }
}
