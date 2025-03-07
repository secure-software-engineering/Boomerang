package test.cases.accesspath;

import test.TestMethod;
import test.core.QueryMethods;
import test.core.selfrunning.AllocatedObject;

@SuppressWarnings("unused")
public class AccessPathTarget {

  private boolean staticallyUnknown() {
    return Math.random() > 0.5;
  }

  private static class A {
    B b = null;
    A g;
  }

  private static class AllocA extends A implements AllocatedObject {}

  private static class B implements AllocatedObject {
    B c = null;
    B d = null;
    public B b;
  }

  private static class C {
    B b = null;
    A attr = new A();
  }

  @TestMethod
  public void getAllAliases() {
    A a = new A();
    B alloc = new B();
    a.b = alloc;
    QueryMethods.accessPathQueryFor(alloc, "a[b]");
  }

  @TestMethod
  public void sameField() {
    AllocA alloc = new AllocA();
    A b = new A();
    A c = new A();
    b.g = alloc;
    c.g = b;
    QueryMethods.accessPathQueryFor(alloc, "b[g];c[g,g]");
  }

  @TestMethod
  public void getAllAliasesBranched() {
    A a = new A();
    A b = new A();
    B alloc = new B();
    if (staticallyUnknown()) {
      a.b = alloc;
    } else {
      b.b = alloc;
    }
    QueryMethods.accessPathQueryFor(alloc, "a[b];b[b]");
  }

  @TestMethod
  public void getAllAliasesLooped() {
    A a = new A();
    B alloc = new B();
    a.b = alloc;
    for (int i = 0; i < 10; i++) {
      B d = alloc;
      alloc.c = d;
    }
    QueryMethods.accessPathQueryFor(alloc, "a[b];alloc[c]*");
  }

  @TestMethod
  public void getAllAliasesLoopedComplex() {
    A a = new A();
    B alloc = new B();
    a.b = alloc;
    for (int i = 0; i < 10; i++) {
      B d = alloc;
      if (staticallyUnknown()) alloc.c = d;
      if (staticallyUnknown()) alloc.d = d;
    }
    QueryMethods.accessPathQueryFor(alloc, "a[b];alloc[c]*;alloc[d]*;alloc[c,d];alloc[d,c]");
  }

  @TestMethod
  public void simpleIndirect() {
    A a = new A();
    A b = a;
    B alloc = new B();
    a.b = alloc;
    QueryMethods.accessPathQueryFor(alloc, "a[b];b[b]");
  }

  @TestMethod
  public void doubleIndirect() {
    C b = new C();
    B alloc = new B();
    b.attr.b = alloc;
    QueryMethods.accessPathQueryFor(alloc, "b[attr,b]");
  }

  @TestMethod
  public void contextQuery() {
    B a = new B();
    B b = a;
    context(a, b);
  }

  private void context(B a, B b) {
    QueryMethods.accessPathQueryFor(a, "a;b");
  }

  @TestMethod
  public void doubleContextQuery() {
    B a = new B();
    B b = a;
    context1(a, b);
  }

  private void context1(B a, B b) {
    context(a, b);
  }

  static void use(Object b) {}

  @TestMethod
  public void twoLevelTest() {
    C b = new C();
    taintMe(b);
  }

  @TestMethod
  public void threeLevelTest() {
    C b = new C();
    taintOnNextLevel(b);
  }

  private void taintMe(C b) {
    B alloc = new B();
    b.attr.b = alloc;
    QueryMethods.accessPathQueryFor(alloc, "alloc;b[attr,b]");
  }

  private void taintOnNextLevel(C b) {
    taintMe(b);
  }

  @TestMethod
  public void hiddenFieldLoad() {
    ClassWithField a = new ClassWithField();
    a.field = new ObjectOfInterest();
    ClassWithField b = a;
    NestedClassWithField n = new NestedClassWithField();
    n.nested = b;
    staticCallOnFile(a, n);
  }

  private static void staticCallOnFile(ClassWithField x, NestedClassWithField n) {
    ObjectOfInterest queryVariable = x.field;
    // The analysis triggers a query for the following variable
    QueryMethods.accessPathQueryFor(queryVariable, "x[field];n[nested,field]");
  }

  public static class ClassWithField {
    public ObjectOfInterest field;
  }

  public static class ObjectOfInterest implements AllocatedObject {}

  public static class NestedClassWithField {
    public ClassWithField nested;
  }

  @TestMethod
  public void hiddenFieldLoad2() {
    ObjectOfInterest alloc = new ObjectOfInterest();
    NestedClassWithField n = new NestedClassWithField();
    store(n, alloc);
    QueryMethods.accessPathQueryFor(alloc, "n[nested,field]");
  }

  private void store(NestedClassWithField o1, ObjectOfInterest oOfInterest) {
    ClassWithField a = new ClassWithField();
    a.field = oOfInterest;
    ClassWithField b = a;
    o1.nested = b;
  }

  @TestMethod
  public void hiddenFieldLoad3() {
    ObjectOfInterest alloc = new ObjectOfInterest();
    NestedClassWithField n = new NestedClassWithField();
    NestedClassWithField t = n;
    store(n, alloc);
    QueryMethods.accessPathQueryFor(alloc, "n[nested,field];t[nested,field]");
    use(t);
  }

  @TestMethod
  public void hiddenFieldLoad4() {
    ObjectOfInterest alloc = new ObjectOfInterest();
    NestedClassWithField n = new NestedClassWithField();
    NestedClassWithField t = n;
    store(n, alloc);
    load(t);
  }

  private void load(NestedClassWithField t) {
    QueryMethods.queryFor(t.nested.field);
  }
}
