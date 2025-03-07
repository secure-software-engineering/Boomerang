package test.cases.sets;

import test.TestMethod;
import test.cases.fields.Alloc;
import test.core.QueryMethods;

public class CustomMapTarget {

  @TestMethod
  public void storeAndLoad() {
    Alloc alloc = new Alloc();
    Map map = new Map();
    map.add(alloc);
    Object alias = map.get();
    QueryMethods.queryFor(alias);
  }

  @TestMethod
  public void storeAndLoadSimple() {
    Alloc alloc = new Alloc();
    Map map = new Map();
    map.add(alloc);
    Object alias = map.m.content;
    QueryMethods.queryFor(alias);
  }

  @TestMethod
  public void onlyInnerMapSimple() {
    Alloc alloc = new Alloc();
    InnerMap map = new InnerMap();
    map.innerAdd(alloc);
    Object alias = map.content;
    QueryMethods.queryFor(alias);
  }

  public static class Map {
    public InnerMap m = new InnerMap();

    public void add(Object o) {
      InnerMap map = this.m;
      map.innerAdd(o);
      InnerMap alias = this.m;
      Object retrieved = alias.content;
    }

    public Object get() {
      InnerMap map = this.m;
      return map.get();
    }
  }

  public static class InnerMap {
    public Object content = null;

    public void innerAdd(Object o) {
      content = o;
    }

    public Object get() {
      return content;
    }
  }

  @TestMethod
  public void storeAndLoadSimpleNoInnerClasses() {
    Alloc alloc = new Alloc();
    MyMap map = new MyMap();
    map.add(alloc);
    MyInnerMap load = map.m;
    Object alias = load.content;
    QueryMethods.queryFor(alias);
  }
}
