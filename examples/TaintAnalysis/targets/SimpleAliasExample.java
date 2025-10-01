import taints.SinkClass;
import taints.SourceClass;

public class SimpleAliasExample {

  public static void main(String[] args) {
    String s = SourceClass.source();
    String z = s;
    SinkClass.sink(z);
  }
}
