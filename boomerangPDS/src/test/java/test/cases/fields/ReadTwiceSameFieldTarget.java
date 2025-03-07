package test.cases.fields;

import test.TestMethod;
import test.core.QueryMethods;
import test.core.selfrunning.AllocatedObject;

@SuppressWarnings("unused")
public class ReadTwiceSameFieldTarget {

  @TestMethod
  public void recursiveTest() {
    Container a = new Container();
    Container c = a.d;
    Container alias = c.d;
    QueryMethods.queryFor(alias);
  }

  @TestMethod
  public void readFieldTwice() {
    Container a = new Container();
    Container c = a.d;
    Container alias = c.d;
    QueryMethods.queryFor(alias);
  }

  private class Container {
    Container d;

    Container() {
      if (Math.random() > 0.5) d = new Alloc();
      else d = null;
    }
  }

  private class DeterministicContainer {
    DeterministicContainer d;

    DeterministicContainer() {
      d = new DeterministicAlloc();
    }
  }

  private class DeterministicAlloc extends DeterministicContainer implements AllocatedObject {}

  private class Alloc extends Container implements AllocatedObject {}
}
