import taints.SinkClass;
import taints.SourceClass;

/**
 * Simple example for a branched dataflow where there are multiple branches that taint the variable.
 * Hence, Boomerang reports both positions for the taint.
 */
public class BranchingMultipleExample {

  private static boolean staticallyUnknown() {
    return Math.random() > 0.5;
  }

  public static void main(String[] args) {
    // Taint position 1
    String s = SourceClass.source();

    if (staticallyUnknown()) {
      // Taint position 2
      s = SourceClass.source();
    }

    SinkClass.sink(s);
  }
}
