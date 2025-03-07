package test.cases.sets;

import java.util.HashMap;
import java.util.Map;
import test.TestMethod;
import test.cases.fields.Alloc;
import test.core.QueryMethods;
import test.core.selfrunning.AllocatedObject;

@SuppressWarnings("unused")
public class HashMapGetLongTarget {

  @TestMethod
  public void addAndRetrieve() {
    Map<Object, Object> map = new HashMap<>();
    Object key = new Object();
    AllocatedObject alias3 = new Alloc();
    map.put(key, alias3);
    Object query = map.get(key);
    QueryMethods.queryFor(query);
  }

  @TestMethod
  public void addAndLoadFromOther() {
    Map<Object, Object> map = new HashMap<>();
    Object key = new Object();
    Object loadKey = new Object();
    AllocatedObject alias3 = new Alloc();
    map.put(key, alias3);
    Object query = map.get(loadKey);
    QueryMethods.queryFor(query);
  }
}
