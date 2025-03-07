package test.cases.lists;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import test.TestMethod;
import test.cases.fields.Alloc;
import test.core.QueryMethods;

@SuppressWarnings("unused")
public class ArrayAndLinkedListsTarget {

  @TestMethod
  public void addAndRetrieve() {
    List<Object> list1 = new LinkedList<>();
    Object o = new Alloc();
    add(list1, o);
    Object o2 = new Object();
    List<Object> list2 = new ArrayList<>();
    add(list2, o2);
    QueryMethods.queryFor(o);
  }

  private void add(List<Object> list1, Object o) {
    list1.add(o);
  }
}
