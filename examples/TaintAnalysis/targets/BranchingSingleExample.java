import taints.SinkClass;
import taints.SourceClass;

/**
 * Simple example for a branched dataflow where there is a single branch that taints the variable.
 * Hence, Boomerang reports one position for a potential leak.
 */
public class BranchingSingleExample {

  private static boolean staticallyUnknown() {
    return Math.random() > 0.5;
  }

  public static void main(String[] args) {
    // No taint position
    String s = SourceClass.noSource();

    if (staticallyUnknown()) {
      // Taint position
      s = SourceClass.source();
    }

    SinkClass.sink(s);
  }
}
