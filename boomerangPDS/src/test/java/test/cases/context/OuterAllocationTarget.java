package test.cases.context;

import test.TestMethod;
import test.core.QueryMethods;
import test.core.selfrunning.AllocatedObject;

@SuppressWarnings("unused")
public class OuterAllocationTarget {

  @TestMethod
  public void mainTest() {
    ObjectWithField container = new ObjectWithField();
    container.field = new File();
    ObjectWithField otherContainer = new ObjectWithField();
    File a = container.field;
    otherContainer.field = a;
    flows(container);
  }

  private void flows(ObjectWithField container) {
    File field = container.field;
    field.open();
    QueryMethods.queryFor(field);
  }

  private static class File implements AllocatedObject {
    public void open() {}
  }

  private static class ObjectWithField {
    File field;
  }
}
