package test.cases.sets;

import java.util.HashMap;
import java.util.Map;
import test.TestMethod;
import test.cases.fields.Alloc;
import test.core.QueryMethods;
import test.core.selfrunning.AllocatedObject;

@SuppressWarnings("unused")
public class HashMapsLongTarget {

  @TestMethod
  public void addAndRetrieve() {
    Map<Object, Object> set = new HashMap<>();
    Object key = new Object();
    AllocatedObject alias3 = new Alloc();
    set.put(key, alias3);
    Object alias2 = null;
    for (Object o : set.values()) alias2 = o;
    Object ir = alias2;
    Object query2 = ir;
    QueryMethods.queryFor(query2);
  }
}
