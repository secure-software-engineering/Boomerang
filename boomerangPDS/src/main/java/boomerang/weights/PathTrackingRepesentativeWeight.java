package boomerang.weights;

import boomerang.scope.ControlFlowGraph.Edge;
import boomerang.scope.Val;
import java.util.LinkedHashSet;
import javax.annotation.Nonnull;
import sync.pds.solver.nodes.Node;
import wpds.impl.Weight;

public class PathTrackingRepesentativeWeight implements PathTrackingWeight {

  @Nonnull private static final PathTrackingRepesentativeWeight one = new PathTrackingRepesentativeWeight();

  private PathTrackingRepesentativeWeight() {
    /*  Singleton */
  }

  public static PathTrackingRepesentativeWeight getInstanceOne() {
    return one;
  }

  @Nonnull
  @Override
  public LinkedHashSet<Node<Edge, Val>> getShortestPathWitness() {
    throw new IllegalStateException("don't!");
  }

  @Override
  @Nonnull
  public Weight extendWith(@Nonnull Weight o) {
    throw new IllegalStateException("This should not happen!");
  }

  @Override
  @Nonnull
  public Weight combineWith(@Nonnull Weight o) {
    throw new IllegalStateException("This should not happen!");
  }

  @Override
  public int hashCode() {
    return toString().hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    return this == one;
  }

  @Override
  public String toString() {
    return "ONE";
  }

}
