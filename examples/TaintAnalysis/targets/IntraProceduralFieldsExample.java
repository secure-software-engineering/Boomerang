import taints.SinkClass;
import taints.SourceClass;

public class IntraProceduralFieldsExample {

  private static class C {
    String field;
  }

  public static void main(String[] args) {
    String s = SourceClass.source();

    C a = new C();
    a.field = s;

    C b = a;
    SinkClass.sink(b.field);
  }
}
