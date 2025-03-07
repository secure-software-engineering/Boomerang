package test.cases.synchronizd;

import test.TestMethod;
import test.cases.fields.Alloc;
import test.core.QueryMethods;
import test.core.selfrunning.AllocatedObject;

@SuppressWarnings("unused")
public class BlockTarget {

  private Object field;

  @TestMethod
  public void block() {
    synchronized (field) {
      AllocatedObject o = new Alloc();
      QueryMethods.queryFor(o);
    }
  }

  @TestMethod
  public void block2() {
    set();
    synchronized (field) {
      Object o = field;
      QueryMethods.queryFor(o);
    }
  }

  private void set() {
    synchronized (field) {
      field = new Alloc();
    }
  }
}
