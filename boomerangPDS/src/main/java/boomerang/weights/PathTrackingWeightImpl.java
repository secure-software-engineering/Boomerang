package boomerang.weights;

import boomerang.scope.ControlFlowGraph.Edge;
import boomerang.scope.Val;
import com.google.common.collect.Lists;

import java.util.LinkedHashSet;
import java.util.List;
import javax.annotation.Nonnull;
import sync.pds.solver.nodes.Node;
import wpds.impl.Weight;

public class PathTrackingWeightImpl implements PathTrackingWeight {

  /**
   * This set keeps track of all statements on a shortest path that use an alias from source to
   * sink.
   */
  @Nonnull private final LinkedHashSet<Node<Edge, Val>> shortestPathWitness;

  private PathTrackingWeightImpl(
      LinkedHashSet<Node<Edge, Val>> allStatement) {
    this.shortestPathWitness = allStatement;
  }

  public PathTrackingWeightImpl(Node<Edge, Val> relevantStatement) {
    this.shortestPathWitness = new LinkedHashSet<>();
    this.shortestPathWitness.add(relevantStatement);
  }



  @Nonnull
  @Override
  public Weight extendWith(@Nonnull Weight o) {
    if (!(o instanceof PathTrackingWeightImpl)) {
        throw new RuntimeException("Cannot extend to different types of weight!");
    }

    PathTrackingWeightImpl other = (PathTrackingWeightImpl) o;
    LinkedHashSet<Node<Edge, Val>> newAllStatements = new LinkedHashSet<>();
    newAllStatements.addAll(shortestPathWitness);
    newAllStatements.addAll(other.shortestPathWitness);

    return new PathTrackingWeightImpl(newAllStatements);
  }

  @Nonnull
  @Override
  public Weight combineWith(@Nonnull Weight o) {
    if (!(o instanceof PathTrackingWeightImpl)) {
        throw new RuntimeException("Cannot extend to different types of weight!");
    }
    PathTrackingWeightImpl other = (PathTrackingWeightImpl) o;

    if (shortestPathWitness.size() > other.shortestPathWitness.size()) {
      return new PathTrackingWeightImpl(
          new LinkedHashSet<>(other.shortestPathWitness));
    }

    return new PathTrackingWeightImpl(
        new LinkedHashSet<>(this.shortestPathWitness));
  }

  @Override
  public int hashCode() {
    return shortestPathWitness.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
        return true;
    }
    if (obj == null) {
        return false;
    }
    if (getClass() != obj.getClass()) {
        return false;
    }
    PathTrackingWeightImpl other = (PathTrackingWeightImpl) obj;
    return shortestPathWitness.equals(other.shortestPathWitness);
  }

  @Override
  public String toString() {
    return "\nAll statements: " + shortestPathWitness;
  }

  @Nonnull
  public List<Node<Edge, Val>> getShortestPathWitness() {
    // TODO: [ms] maybe don't copy but return as unmodifiable?
    return Lists.newArrayList(shortestPathWitness);
  }

}
