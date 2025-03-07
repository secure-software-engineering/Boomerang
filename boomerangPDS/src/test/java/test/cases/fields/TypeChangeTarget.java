package test.cases.fields;

import test.TestMethod;
import test.core.QueryMethods;
import test.core.selfrunning.AllocatedObject;

@SuppressWarnings("unused")
public class TypeChangeTarget {

  @TestMethod
  public void returnValue() {
    D f = new D();
    Object amIThere = f.getField();
    QueryMethods.queryFor(amIThere);
  }

  @TestMethod
  public void doubleReturnValue() {
    D f = new D();
    Object t = f.getDoubleField();
    QueryMethods.queryFor(t);
  }

  @TestMethod
  public void returnValueAndBackCast() {
    D f = new D();
    Object t = f.getField();
    AllocatedObject u = (AllocatedObject) t;
    QueryMethods.queryFor(u);
  }

  public static class D {
    Alloc f = new Alloc();
    D d = new D();

    public Object getField() {
      Alloc varShouldBeThere = this.f;
      return varShouldBeThere;
    }

    public Object getDoubleField() {
      return d.getField();
    }
  }
}
