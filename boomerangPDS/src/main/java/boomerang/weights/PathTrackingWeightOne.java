package boomerang.weights;

import boomerang.scope.ControlFlowGraph.Edge;
import boomerang.scope.Val;
import java.util.LinkedHashSet;
import javax.annotation.Nonnull;
import sync.pds.solver.nodes.Node;
import wpds.impl.Weight;

public class PathTrackingWeightOne implements PathTrackingWeight {

  @Nonnull private static final PathTrackingWeightOne one = new PathTrackingWeightOne();

  private PathTrackingWeightOne() {
    /*  Singleton */
  }

  public static PathTrackingWeightOne one() {
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
  public boolean equals(Object obj) {
    return this == one;
  }

  @Override
  public String toString() {
    return "ONE";
  }
}
