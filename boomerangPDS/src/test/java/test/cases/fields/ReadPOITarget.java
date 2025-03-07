package test.cases.fields;

import test.TestMethod;
import test.core.QueryMethods;
import test.core.selfrunning.AllocatedObject;

@SuppressWarnings("unused")
public class ReadPOITarget {

  public class A {
    Alloc b = null;
  }

  @TestMethod
  public void indirectAllocationSite() {
    A a = new A();
    A e = a;
    e.b = new Alloc();
    Alloc query = a.b;
    QueryMethods.queryFor(query);
  }

  @TestMethod
  public void indirectAllocationSiteTwoFields3Address() {
    Node a = new Node();
    Node firstLoad = a.left;
    AllocNode alloc = new AllocNode();
    firstLoad.right = alloc;
    Node secondLoad = a.left;
    Node query = secondLoad.right;
    QueryMethods.queryFor(query);
  }

  @TestMethod
  public void indirectAllocationSiteTwoFields3Address2() {
    EmptyNode a = new EmptyNode();
    a.left = new EmptyNode();
    EmptyNode firstLoad = a.left;
    EmptyAllocNode alloc = new EmptyAllocNode();
    firstLoad.right = alloc;
    EmptyNode secondLoad = a.left;
    EmptyNode query = secondLoad.right;
    QueryMethods.queryFor(query);
  }

  private static class EmptyNode {
    EmptyNode left;
    EmptyNode right;
  }

  private static class EmptyAllocNode extends EmptyNode implements AllocatedObject {}

  @TestMethod
  public void unbalancedField() {
    OWithField a = new OWithField();
    Object query = a.field;
    QueryMethods.queryFor(query);
  }

  @TestMethod
  public void loadTwice() {
    OWithRecField a = new OWithRecField();
    OWithRecField query = a.field.field;
    QueryMethods.queryFor(query);
  }

  private static class OWithRecField {
    OWithRecField field = new AllocRec();
  }

  private static class AllocRec extends OWithRecField {}

  private static class OWithField {
    Object field = new Alloc();
  }

  @TestMethod
  public void indirectAllocationSiteTwoFields() {
    Node a = new Node();
    a.left.right = new AllocNode();
    Node query = a.left.right;
    QueryMethods.queryFor(query);
  }

  @TestMethod
  public void twoFieldsBranched() {
    Node a = new Node();
    init(a);
    Node query = null;
    if (Math.random() > 0.5) query = a.left;
    else query = a.right;
    QueryMethods.queryFor(query);
  }

  private void init(Node a) {
    a.left = new AllocNode();
    a.right = new AllocNode();
  }

  @TestMethod
  public void oneFieldBranched() {
    A a = new A();
    set(a);
    Alloc query = a.b;
    QueryMethods.queryFor(query);
  }

  private void set(A p) {
    p.b = new Alloc();
  }

  @TestMethod
  public void overwriteFieldWithItself() {
    List query = new List();
    query = query.next;
    QueryMethods.queryFor(query);
  }

  private class List {
    List next = new AllocListElement();
  }

  private class AllocListElement extends List implements AllocatedObject {}

  private static class Node {
    Node left = new Node();
    Node right;
  }

  private static class AllocNode extends Node implements AllocatedObject {}

  private static class Alloc implements AllocatedObject {}
}
