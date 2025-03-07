package test.cases.generics;

import test.TestMethod;
import test.core.QueryMethods;
import test.core.selfrunning.AllocatedObject;

@SuppressWarnings("unused")
public class GenericsTarget {

  @TestMethod
  public void genericFieldAccess() {
    GenericClass<GenericType> c = new GenericClass<>();
    GenericType genType = new GenericType();
    c.setField(genType);
    GenericType query = c.getField();
    QueryMethods.queryFor(query);
  }

  @TestMethod
  public void genericFieldAccessWrapped() {
    WrappedGenericClass<GenericType> c = new WrappedGenericClass<>();
    GenericType genType = new GenericType();
    c.setField(genType);
    GenericType query = c.getField();
    QueryMethods.queryFor(query);
  }

  public static class GenericClass<T> {
    T field;

    public void setField(T t) {
      field = t;
    }

    public T getField() {
      return field;
    }
  }

  public static class GenericType implements AllocatedObject {}

  public static class WrappedGenericClass<T> {
    GenericClass<T> gen = new GenericClass<>();

    public void setField(T t) {
      gen.setField(t);
    }

    public T getField() {
      return gen.getField();
    }
  }
}
