package test.cases.sets;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import test.TestMethod;
import test.cases.fields.Alloc;
import test.core.QueryMethods;

@SuppressWarnings("unused")
public class TreeSetsLongTarget {

  @TestMethod
  public void addAndRetrieve() {
    Set<Object> set = new TreeSet<>();
    Alloc alias = new Alloc();
    set.add(alias);
    alias = new Alloc();
    set.add(alias);

    Object alias2 = null;
    for (Object o : set) alias2 = o;
    Object ir = alias2;
    Object query2 = ir;
    Set<Object> set2 = new TreeSet<>();
    Object alias1 = new Object();
    set2.add(alias1);
    alias1 = new Object();
    set2.add(alias1);
    alias1 = new Object();
    QueryMethods.queryFor(query2);
    otherMap();
    otherMap2();
    hashMap();
  }

  private void hashMap() {
    HashSet<Object> map = new HashSet<>();
    map.add(new Object());
    map = new HashSet<>();
    map.add(new Object());
    map = new HashSet<>();
    map.add(new Object());
    map = new HashSet<>();
    map.add(new Object());
    map = new HashSet<>();
    map.add(new Object());
    map = new HashSet<>();
    map.add(new Object());
    map = new HashSet<>();
    map.add(new Object());
    map = new HashSet<>();
    map.add(new Object());
    map = new HashSet<>();
    map.add(new Object());
    map = new HashSet<>();
    map.add(new Object());
    map = new HashSet<>();
    map.add(new Object());
    map = new HashSet<>();
    map.add(new Object());
    map = new HashSet<>();
    map.add(new Object());
    map = new HashSet<>();
    map.add(new Object());
    map = new HashSet<>();
    map.add(new Object());
  }

  private void otherMap2() {
    Set<Object> set = new TreeSet<>();
    Object alias = new Object();
    set.add(alias);
    alias = new Object();
    set.add(alias);
    alias = new Object();
    set.add(alias);
    alias = new Object();
    set.add(alias);
    alias = new Object();
    set.add(alias);

    Object alias2 = null;
    for (Object o : set) alias2 = o;
  }

  private void otherMap() {
    Set<Object> set = new TreeSet<>();
    Object alias = new Object();
    set.add(alias);
    alias = new Object();
    set.add(alias);
    alias = new Object();
    set.add(alias);
    alias = new Object();
    set.add(alias);
    alias = new Object();
    set.add(alias);

    Object alias2 = null;
    for (Object o : set) alias2 = o;
  }
}
