package test.cases.sets;

import java.util.HashSet;
import test.TestMethod;
import test.core.QueryMethods;
import test.core.selfrunning.AllocatedObject;

@SuppressWarnings("unused")
public class HashSetsLongTarget {

  @TestMethod
  public void addAndRetrieve() {
    HashSet<Object> set = new HashSet<>();
    AllocatedObject alias = new AllocatedObject() {};
    AllocatedObject alias3 = new AllocatedObject() {};
    set.add(alias);
    set.add(alias3);
    Object alias2 = null;
    for (Object o : set) alias2 = o;
    Object ir = alias2;
    Object query2 = ir;
    QueryMethods.queryFor(query2);
  }
}
