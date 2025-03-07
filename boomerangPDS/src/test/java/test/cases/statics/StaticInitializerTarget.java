package test.cases.statics;

import test.TestMethod;
import test.cases.fields.Alloc;
import test.core.QueryMethods;

@SuppressWarnings("unused")
public class StaticInitializerTarget {

  private static final Object alloc = new Alloc();

  @TestMethod
  public void doubleSingleton() {
    Object alias = alloc;
    QueryMethods.queryFor(alias);
  }
}
