package boomerang.weights;

import boomerang.scope.ControlFlowGraph.Edge;
import boomerang.scope.Val;
import com.google.common.collect.Sets;
import sync.pds.solver.nodes.Node;
import wpds.impl.Weight;

import javax.annotation.Nonnull;
import java.util.LinkedHashSet;
import java.util.Set;

public class PathTrackingRepesentativeWeight implements Weight {

  private static PathTrackingRepesentativeWeight one;

  /**
   * This set keeps track of all statement along all paths that use an alias from source to sink.
   */
  private Set<LinkedHashSet<Node<Edge, Val>>> allPathWitness = Sets.newHashSet();

  private String rep;

  private PathTrackingRepesentativeWeight(String rep) {
    this.rep = rep;
  }

  private PathTrackingRepesentativeWeight(
      Set<LinkedHashSet<Node<Edge, Val>>> allPathWitness) {
    this.allPathWitness = allPathWitness;
  }


  public static <W extends Weight> W one() {
    if (one == null) {
      one = new PathTrackingRepesentativeWeight("ONE");
    }
    return (W) one;
  }

  @Override
  public Weight extendWith(Weight o) {
    if (!(o instanceof PathTrackingRepesentativeWeight))
      throw new RuntimeException("Cannot extend to different types of weight!");
    PathTrackingRepesentativeWeight other = (PathTrackingRepesentativeWeight) o;


      Set<LinkedHashSet<Node<Edge, Val>>> newAllPathStatements = new LinkedHashSet<>();
    for (LinkedHashSet<Node<Edge, Val>> pathPrefix : allPathWitness) {
      for (LinkedHashSet<Node<Edge, Val>> pathSuffix : other.allPathWitness) {
        LinkedHashSet<Node<Edge, Val>> combinedPath = Sets.newLinkedHashSet();
        combinedPath.addAll(pathPrefix);
        combinedPath.addAll(pathSuffix);
        newAllPathStatements.add(combinedPath);
      }
    }
    if (allPathWitness.isEmpty()) {
      for (LinkedHashSet<Node<Edge, Val>> pathSuffix : other.allPathWitness) {
        LinkedHashSet<Node<Edge, Val>> combinedPath = Sets.newLinkedHashSet();
        combinedPath.addAll(pathSuffix);
        newAllPathStatements.add(combinedPath);
      }
    }
    if (other.allPathWitness.isEmpty()) {
      for (LinkedHashSet<Node<Edge, Val>> pathSuffix : allPathWitness) {
        LinkedHashSet<Node<Edge, Val>> combinedPath = Sets.newLinkedHashSet();
        combinedPath.addAll(pathSuffix);
        newAllPathStatements.add(combinedPath);
      }
    }

    return new PathTrackingRepesentativeWeight(newAllPathStatements);
  }

  @Override
  public Weight combineWith(Weight o) {
    if (!(o instanceof PathTrackingRepesentativeWeight))
      throw new RuntimeException("Cannot extend to different types of weight!");
    PathTrackingRepesentativeWeight other = (PathTrackingRepesentativeWeight) o;
    Set<LinkedHashSet<Node<Edge, Val>>> newAllPathStatements = new LinkedHashSet<>();
    for (LinkedHashSet<Node<Edge, Val>> pathPrefix : allPathWitness) {
      LinkedHashSet<Node<Edge, Val>> combinedPath = Sets.newLinkedHashSet();
      combinedPath.addAll(pathPrefix);
      newAllPathStatements.add(combinedPath);
    }
    for (LinkedHashSet<Node<Edge, Val>> pathPrefix : other.allPathWitness) {
      LinkedHashSet<Node<Edge, Val>> combinedPath = Sets.newLinkedHashSet();
      combinedPath.addAll(pathPrefix);
      newAllPathStatements.add(combinedPath);
    }


    throw new IllegalStateException("This should not happen!");
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ( rep.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    PathTrackingRepesentativeWeight other = (PathTrackingRepesentativeWeight) obj;

    if (allPathWitness == null) {
      if (other.allPathWitness != null) return false;
    } else if (!allPathWitness.equals(other.allPathWitness)) return false;
     return rep.equals(other.rep);
  }

}
