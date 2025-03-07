package test.cases.callgraph;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import test.TestMethod;
import test.cases.fields.Alloc;
import test.core.QueryMethods;

@SuppressWarnings("unused")
public class ContextSpecificListTypeTarget {

  public void wrongContext() {
    List<Object> list = new WrongList();
    method(list);
  }

  public Object method(List<Object> list) {
    Alloc alloc = new Alloc();
    list.add(alloc);
    return alloc;
  }

  @TestMethod
  public void testListType() {
    wrongContext();
    List<Object> list = new ArrayList<>();
    Object query = method(list);
    QueryMethods.queryFor(query);
  }

  private static class WrongList extends LinkedList<Object> {
    @Override
    public boolean add(Object e) {
      unreachable();
      return false;
    }

    public void unreachable() {}
  }
}
