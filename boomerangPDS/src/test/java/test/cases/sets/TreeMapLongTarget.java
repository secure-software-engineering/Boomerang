package test.cases.sets;

import java.util.Map;
import java.util.TreeMap;
import test.TestMethod;
import test.cases.fields.Alloc;
import test.core.QueryMethods;

@SuppressWarnings("unused")
public class TreeMapLongTarget {

  @TestMethod
  public void addAndRetrieve() {
    Map<Integer, Object> set = new TreeMap<>();
    Alloc alias = new Alloc();
    set.put(1, alias);
    Object query2 = set.get(2);
    QueryMethods.queryFor(query2);
  }
}
