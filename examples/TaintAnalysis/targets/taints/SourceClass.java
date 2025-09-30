package taints;

public class SourceClass {

  public static String source() {
    return "secret";
  }

  public static String noSource() {
    return "noSecret";
  }
}
