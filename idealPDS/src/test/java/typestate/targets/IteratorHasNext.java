package typestate.targets;

import assertions.Assertions;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import test.TestMethod;

@SuppressWarnings("unused")
public class IteratorHasNext {

  @TestMethod
  public void test1() {
    List<Object> list = new LinkedList<>();
    list.add(new Object());
    list.add(new Object());
    for (Object l : list) {
      System.out.println(l);
    }
    Assertions.mustBeInAcceptingState(list.iterator());
  }

  @TestMethod
  public void test2() {
    MyLinkedList<Object> list = new MyLinkedList<>();
    list.add(new Object());
    Iterator<Object> iterator = list.iterator();
    iterator.hasNext();
    iterator.next();
    iterator.next();
    Assertions.mustBeInErrorState(iterator);
  }

  @TestMethod
  public void test3() {
    LinkedList<Object> list = new LinkedList<>();
    list.add(new Object());
    Iterator<Object> it1 = list.iterator();
    Object each = null;
    for (; it1.hasNext(); each = it1.next()) {
      try {
        each.toString();
      } catch (Throwable e) {
        e.getMessage();
      }
    }
    Assertions.mustBeInAcceptingState(it1);
  }

  @TestMethod
  public void test4() {
    List<Object> l1 = new ArrayList<>();
    List<Object> l2 = new ArrayList<>();

    l1.add("foo");
    l1.add("moo");
    l1.add("zoo");

    Object v;
    Iterator<Object> it1 = l1.iterator();
    for (; it1.hasNext(); v = it1.next()) {
      System.out.println(foo(it1));
    }
    Assertions.mayBeInErrorState(it1);
  }

  @TestMethod
  public void chartTest() {
    AxisCollection col = new AxisCollection();
    col.add(new Object());
    Iterator<Object> iterator = col.getAxesAtBottom().iterator();
    while (iterator.hasNext()) {
      Object next = iterator.next();
      next.hashCode();
    }
    iterator = col.getAxesAtTop().iterator();
    Assertions.mustBeInAcceptingState(iterator);
    while (iterator.hasNext()) {
      Assertions.mustBeInAcceptingState(iterator);
      Object next = iterator.next();
      next.hashCode();
      Assertions.mustBeInAcceptingState(iterator);
    }
    Assertions.mustBeInAcceptingState(iterator);
  }

  private static class AxisCollection {
    private final ArrayList<Object> axesAtTop;
    private final ArrayList<Object> axesAtBottom;

    public AxisCollection() {
      this.axesAtTop = new ArrayList<>();
      this.axesAtBottom = new ArrayList<>();
    }

    public void add(Object object) {
      if (Math.random() > 0.5) {
        this.axesAtBottom.add(object);
      } else {
        this.axesAtTop.add(object);
      }
    }

    public ArrayList<Object> getAxesAtBottom() {
      return axesAtBottom;
    }

    public ArrayList<Object> getAxesAtTop() {
      return axesAtTop;
    }
  }

  public Object foo(Iterator<Object> it) {
    return it.next();
  }

  private static class MyLinkedList<V> {

    public void add(Object object) {}

    public Iterator<V> iterator() {
      return new MyIterator<>();
    }
  }

  private static class MyIterator<V> implements Iterator<V> {

    @Override
    public boolean hasNext() {
      return false;
    }

    @Override
    public V next() {
      return null;
    }

    @Override
    public void remove() {}
  }
}
