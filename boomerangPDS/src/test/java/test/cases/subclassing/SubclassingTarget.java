package test.cases.subclassing;

import test.TestMethod;
import test.core.QueryMethods;
import test.core.selfrunning.AllocatedObject;

@SuppressWarnings("unused")
public class SubclassingTarget {

  private static class Superclass {
    AllocatedObject o = new AllocatedObject() {};
  }

  private static class Subclass extends Superclass {}

  private static class ClassWithSubclassField {
    Subclass f;

    public ClassWithSubclassField(Subclass t) {
      this.f = t;
    }
  }

  @TestMethod
  public void typingIssue() {
    Subclass subclass = new Subclass();
    ClassWithSubclassField classWithSubclassField = new ClassWithSubclassField(subclass);
    AllocatedObject query = classWithSubclassField.f.o;
    QueryMethods.queryFor(query);
  }
}
