package test.cases.bugfixes.issue5;

import java.util.LinkedList;
import java.util.List;

public class Test {

  public static List<Foo> foos() {
    Foo foo = new Foo();
    foo.baz();
    System.out.println(foo);
    List<Foo> x = new LinkedList<>();
    x.add(foo);
    foo.bar();
    return x;
  }

  public static void main(String[] args) {
    System.out.println(foos());
  }
}
