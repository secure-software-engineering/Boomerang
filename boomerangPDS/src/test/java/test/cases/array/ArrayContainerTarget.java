package test.cases.array;

import test.TestMethod;
import test.cases.fields.Alloc;
import test.core.QueryMethods;
import test.core.selfrunning.AllocatedObject;
import test.core.selfrunning.NoAllocatedObject;

@SuppressWarnings("unused")
public class ArrayContainerTarget {

  public static class NoAllocation implements NoAllocatedObject {}

  private static class ArrayContainer {
    AllocatedObject[] array = new AllocatedObject[] {};

    void put(Object o) {
      array[0] = (AllocatedObject) o;
    }

    AllocatedObject get() {
      return array[0];
    }
  }

  @TestMethod
  public void insertAndGet() {
    ArrayContainer container = new ArrayContainer();
    Object o1 = new Object();
    container.put(o1);
    AllocatedObject o2 = new Alloc();
    container.put(o2);
    AllocatedObject alias = container.get();
    QueryMethods.queryFor(alias);
  }

  @TestMethod
  public void insertAndGetField() {
    ArrayContainerWithPublicFields container = new ArrayContainerWithPublicFields();
    AllocatedObject o2 = new Alloc();
    container.array[0] = o2;
    AllocatedObject alias = container.array[0];
    QueryMethods.queryFor(alias);
  }

  public static class ArrayContainerWithPublicFields {
    public AllocatedObject[] array = new AllocatedObject[] {};
  }

  @TestMethod
  public void insertAndGetDouble() {
    ArrayOfArrayOfContainers outerContainer = new ArrayOfArrayOfContainers();
    ArrayContainer innerContainer1 = new ArrayContainer();
    Object o1 = new NoAllocation();
    innerContainer1.put(o1);
    AllocatedObject o2 = new Alloc();
    innerContainer1.put(o2);
    outerContainer.put(innerContainer1);
    ArrayContainer innerContainer2 = outerContainer.get();
    AllocatedObject alias = innerContainer2.get();
    QueryMethods.queryFor(alias);
  }

  private static class ArrayOfArrayOfContainers {
    ArrayContainer[] outerArray = new ArrayContainer[] {new ArrayContainer()};

    void put(ArrayContainer other) {
      outerArray[0] = other;
    }

    ArrayContainer get() {
      return outerArray[0];
    }
  }
}
