package test.cases.lists;

import java.util.List;
import java.util.Vector;
import test.TestMethod;
import test.core.QueryMethods;
import test.core.selfrunning.AllocatedObject;

@SuppressWarnings("unused")
public class VectorsLongTarget {

  @TestMethod
  public void addAndRetrieveWithIterator() {
    List<Object> set = new Vector<>();
    AllocatedObject alias = new AllocatedObject() {};
    set.add(alias);
    Object alias2 = null;
    for (Object o : set) alias2 = o;
    Object ir = alias2;
    Object query2 = ir;
    QueryMethods.queryFor(query2);
  }

  @TestMethod
  public void addAndRetrieveByIndex1() {
    List<Object> list = new Vector<>();
    AllocatedObject alias = new AllocatedObject() {};
    list.add(alias);
    Object ir = list.get(0);
    Object query2 = ir;
    QueryMethods.queryFor(query2);
  }

  @TestMethod
  public void addAndRetrieveByIndex2() {
    List<Object> list = new Vector<>();
    AllocatedObject alias = new AllocatedObject() {};
    list.add(alias);
    Object ir = list.get(1);
    Object query2 = ir;
    QueryMethods.queryFor(query2);
  }
}
