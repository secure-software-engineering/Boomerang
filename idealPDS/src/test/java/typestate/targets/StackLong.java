package typestate.targets;

import assertions.Assertions;
import java.util.ArrayList;
import java.util.Stack;
import test.TestMethod;

@SuppressWarnings("unused")
public class StackLong {

  private boolean staticallyUnknown() {
    return Math.random() > 0.5;
  }

  @TestMethod
  public void test1() {
    Stack<Object> s = new Stack<>();
    if (staticallyUnknown()) s.peek();
    else {
      Stack<Object> r = s;
      r.pop();
      Assertions.mustBeInErrorState(r);
    }
    Assertions.mustBeInErrorState(s);
  }

  @TestMethod
  public void test4simple() {
    Stack<Object> s = new Stack<>();
    s.peek();
    Assertions.mustBeInErrorState(s);
    s.pop();
    Assertions.mustBeInErrorState(s);
  }

  @TestMethod
  public void test2() {
    Stack<Object> s = new Stack<>();
    s.add(new Object());
    if (staticallyUnknown()) s.peek();
    else s.pop();
    Assertions.mustBeInAcceptingState(s);
  }

  @TestMethod
  public void test6() {
    ArrayList<Object> l = new ArrayList<>();
    Stack<Object> s = new Stack<>();
    if (staticallyUnknown()) {
      s.push(new Object());
    }
    if (staticallyUnknown()) {
      s.push(new Object());
    }
    if (!s.isEmpty()) {
      Object pop = s.pop();
      Assertions.mayBeInErrorState(s);
    }
  }

  @TestMethod
  public void test3() {
    Stack<Object> s = new Stack<>();
    s.peek();
    Assertions.mustBeInErrorState(s);
    s.pop();
    Assertions.mustBeInErrorState(s);
  }

  @TestMethod
  public void test5() {
    Stack<Object> s = new Stack<>();
    s.peek();
    Assertions.mustBeInErrorState(s);
  }

  @TestMethod
  public void test4() {
    Stack<Object> s = new Stack<>();
    s.peek();
    s.pop();

    Stack<Object> c = new Stack<>();
    c.add(new Object());
    c.peek();
    c.pop();
    Assertions.mustBeInErrorState(s);
    Assertions.mustBeInAcceptingState(c);
  }

  @TestMethod
  public void testInNewObject() {
    ObjectWithStack oWithStack = new ObjectWithStack();
    oWithStack.pushStack(new Object());
    oWithStack.get();
    Assertions.mustBeInAcceptingState(oWithStack.stack);
  }

  private static class ObjectWithStack {
    Stack<Object> stack;

    public void pushStack(Object o) {
      if (this.stack == null) {
        this.stack = new Stack<>();
      }
      this.stack.push(o);
    }

    public Object get() {
      if (stack == null || stack.empty()) {
        return null;
      }
      Object peek = this.stack.peek();
      Assertions.mustBeInAcceptingState(this.stack);
      return peek;
    }
  }
}
