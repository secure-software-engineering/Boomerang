package test.cases.statics;

import test.TestMethod;
import test.cases.fields.Alloc;
import test.core.QueryMethods;

@SuppressWarnings("unused")
public class StaticWithSuperClassesTarget {

  @TestMethod
  public void simple() {
    List list = new List();
    Object o = list.get();
    QueryMethods.queryForAndNotEmpty(o);
  }

  private static class List {

    private static final Object elementData = new Alloc();

    public Object get() {
      return elementData;
    }
  }

  @TestMethod
  public void superClass() {
    MyList list = new MyList();
    Object o = list.get();
    QueryMethods.queryForAndNotEmpty(o);
  }

  private static class MyList extends List {

    private static final Object elementData2 = new Alloc();

    public Object get() {
      return elementData2;
    }
  }
}
