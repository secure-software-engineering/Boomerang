package test.cases.string;

import test.TestMethod;
import test.core.QueryMethods;
import test.core.selfrunning.AllocatedObject;

@SuppressWarnings("unused")
public class StringTarget {

  @TestMethod
  public void stringConcat() {
    Object query = "a" + "b";
    if (Math.random() > 0.5) query += "c";
    QueryMethods.queryFor(query);
  }

  @TestMethod
  public void stringConcatQueryByPass() {
    T t = new T("a" + (Math.random() < 0.5));
    QueryMethods.queryFor(t);
  }

  @TestMethod
  public void stringBufferQueryByPass() {
    StringBuffer s = new StringBuffer();
    s.append("");
    s.append("");
    s.append("");
    T t = new T(s.toString());
    StringBuffer t2 = new StringBuffer();
    QueryMethods.queryFor(t);
  }

  private static class T implements AllocatedObject {

    private String string;

    public T(String string) {
      this.string = string;
    }
  }

  @TestMethod
  public void stringToCharArray() {
    char[] s = "password".toCharArray();
    QueryMethods.queryFor(s);
  }

  @TestMethod
  public void stringBuilderTest() {
    StringBuilder b = new StringBuilder("Test");
    b.append("ABC");
    String s = b.toString();
    QueryMethods.queryFor(s);
  }

  @TestMethod
  public void stringBuilder1Test() {
    String alloc = "Test";
    MyStringBuilder b = new MyStringBuilder(alloc);
    String s = b.toString();
    QueryMethods.queryFor(s);
  }

  private static class MyAbstractStringBuilder {
    /** The value is used for character storage. */
    char[] value;

    /** The count is the number of characters used. */
    int count;

    /** This no-arg constructor is necessary for serialization of subclasses. */
    MyAbstractStringBuilder() {}

    /** Creates an AbstractStringBuilder of the specified capacity. */
    MyAbstractStringBuilder(int capacity) {
      value = new char[capacity];
    }

    public MyAbstractStringBuilder append(String str) {
      // if (str == null)
      // return appendNull();
      int len = str.length();
      // ensureCapacityInternal(count + len);
      str.getChars(0, len, value, count);
      count += len;
      return this;
    }
  }

  private static class MyStringBuilder extends MyAbstractStringBuilder {
    public MyStringBuilder(String str) {
      super(str.length() + 16);
      append(str);
    }

    @Override
    public String toString() {
      // Create a copy, don't share the array
      return new String(value, 0, count);
    }
  }
}
