package test.cases.basic;

import test.TestMethod;
import test.cases.fields.Alloc;
import test.core.QueryMethods;
import test.core.selfrunning.AllocatedObject;

@SuppressWarnings("unused")
public class InterproceduralTarget {

  @TestMethod
  public void identityTest() {
    AllocatedObject alias1 = new AllocatedObject() {};
    AllocatedObject alias2 = identity(alias1);
    QueryMethods.queryFor(alias2);
  }

  @TestMethod
  public void simpleAnonymous() {
    AllocatedObject alias1 = new AllocatedObject() {};
    QueryMethods.queryFor(alias1);
  }

  @TestMethod
  public void simpleNonAnonymous() {
    AllocatedObject alias1 = new Alloc();
    QueryMethods.queryFor(alias1);
  }

  @TestMethod
  public void identityTest1() {
    Alloc alias1 = new Alloc();
    Object alias2 = alias1;
    identity(alias1);
    otherCall(alias2);
    QueryMethods.queryFor(alias1);
  }

  private void otherCall(Object alias2) {}

  @TestMethod
  public void summaryReuseTest1() {
    AllocatedObject alias1 = new AllocatedObject() {}, alias2, alias3, alias4;
    alias2 = identity(alias1);
    alias3 = identity(alias2);
    alias4 = alias1;
    QueryMethods.queryFor(alias4);
  }

  @TestMethod
  public void failedCast() {
    Object o = new Object();
    Object returned = flow(o);
    String t = (String) returned;
    QueryMethods.queryFor(t);
  }

  private Object flow(Object o) {
    return o;
  }

  @TestMethod
  public void summaryReuseTest4() {
    Alloc alias2;
    if (Math.random() > 0.5) {
      Alloc alias1 = new Alloc();
      alias2 = nestedIdentity(alias1);
    } else {
      Alloc alias1 = new Alloc();
      alias2 = nestedIdentity(alias1);
    }
    QueryMethods.queryFor(alias2);
  }

  @TestMethod
  public void branchWithCall() {
    Alloc a1 = new Alloc();
    Alloc a2 = new Alloc();
    Object a = null;
    if (Math.random() > 0.5) {
      a = a1;
    } else {
      a = a2;
    }
    wrappedFoo(a);
    QueryMethods.queryFor(a);
  }

  private void wrappedFoo(Object param) {}

  private Alloc nestedIdentity(Alloc param2) {
    int shouldNotSeeThis = 1;
    Alloc returnVal = param2;
    return returnVal;
  }

  @TestMethod
  public void summaryReuseTest2() {
    AllocatedObject alias1 = new AllocatedObject() {}, alias2, alias3, alias4;
    alias2 = identity(alias1);
    alias3 = identity(alias2);
    alias4 = alias1;
    QueryMethods.queryFor(alias3);
  }

  @TestMethod
  public void summaryReuseTest3() {
    AllocatedObject alias1 = new AllocatedObject() {}, alias2, alias3, alias4;
    alias2 = identity(alias1);
    alias3 = identity(alias2);
    alias4 = alias1;
    QueryMethods.queryFor(alias2);
  }

  @TestMethod
  public void interLoop() {
    AllocatedObject alias = new Alloc() {};
    AllocatedObject aliased2;
    Object aliased = new AllocatedObject() {}, notAlias = new Object();
    for (int i = 0; i < 20; i++) {
      aliased = identity(alias);
    }
    aliased2 = (AllocatedObject) aliased;
    QueryMethods.queryFor(aliased);
  }

  @TestMethod
  public void wrappedAllocationSite() {
    AllocatedObject alias1 = wrappedCreate();
    QueryMethods.queryFor(alias1);
  }

  @TestMethod
  public void branchedObjectCreation() {
    Object alias1;
    if (Math.random() > 0.5) alias1 = create();
    else {
      AllocatedObject intermediate = create();
      alias1 = intermediate;
    }
    Object query = alias1;
    QueryMethods.queryFor(query);
  }

  @TestMethod
  public void unbalancedCreation() {
    Object alias1 = create();
    Object query = alias1;
    QueryMethods.queryFor(query);
  }

  @TestMethod
  public void unbalancedCreationStatic() {
    Object alias1 = createStatic();
    Object query = alias1;
    QueryMethods.queryFor(query);
  }

  private Object createStatic() {
    return new Allocation();
  }

  public AllocatedObject wrappedCreate() {
    return create();
  }

  public AllocatedObject create() {
    AllocatedObject alloc1 = new AllocatedObject() {};
    return alloc1;
  }

  private AllocatedObject identity(AllocatedObject param) {
    AllocatedObject mapped = param;
    return mapped;
  }

  @TestMethod
  public void heavySummary() {
    Allocation alias1 = new Allocation();
    Object q;
    if (Math.random() > 0.5) {
      q = doSummarize(alias1);
    } else if (Math.random() > 0.5) {
      Allocation alias2 = new Allocation();
      q = doSummarize(alias2);
    } else {
      Allocation alias3 = new Allocation();
      q = doSummarize(alias3);
    }

    QueryMethods.queryFor(q);
  }

  private Allocation doSummarize(Allocation alias1) {
    Allocation a = alias1;
    Allocation b = a;
    Allocation c = b;
    Allocation d = c;

    Allocation e = d;
    Allocation f = evenFurtherNested(e);
    Allocation g = alias1;
    if (Math.random() > 0.5) {
      g = f;
    }
    Allocation h = g;
    return f;
  }

  private Allocation evenFurtherNested(Allocation e) {
    return e;
  }

  @TestMethod
  public void summaryTest() {
    Allocation alias1 = new Allocation();
    Object q;
    if (Math.random() > 0.5) {
      q = summary(alias1);
    } else {
      Allocation alias2 = new Allocation();
      q = summary(alias2);
    }

    QueryMethods.queryFor(q);
  }

  private Object summary(Allocation inner) {
    Allocation ret = inner;
    return ret;
  }

  @TestMethod
  public void doubleNestedSummary() {
    Allocation alias1 = new Allocation();
    Object q;
    if (Math.random() > 0.5) {
      q = nestedSummary(alias1);
    } else {
      Allocation alias2 = new Allocation();
      q = nestedSummary(alias2);
    }

    QueryMethods.queryFor(q);
  }

  private Object nestedSummary(Allocation inner) {
    Object ret = summary(inner);
    return ret;
  }
}
