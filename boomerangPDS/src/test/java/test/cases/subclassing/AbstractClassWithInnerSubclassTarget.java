package test.cases.subclassing;

import test.TestMethod;
import test.core.QueryMethods;
import test.core.selfrunning.AllocatedObject;

@SuppressWarnings("unused")
public class AbstractClassWithInnerSubclassTarget {

  private static class Superclass {
    Element e;
  }

  private static class Subclass extends Superclass {
    Subclass() {
      e = new Subclass.InnerClass();
    }

    private static class InnerClass implements Element {
      AnotherClass c = new AnotherClass();

      @Override
      public AnotherClass get() {
        return c;
      }
    }
  }

  private static class AnotherClass {
    AllocatedObject o = new AllocatedObject() {};
  }

  @TestMethod
  public void typingIssue() {
    Subclass subclass2 = new Subclass();
    AllocatedObject query = subclass2.e.get().o;
    QueryMethods.queryFor(query);
  }

  private interface Element {
    AnotherClass get();
  }
}
