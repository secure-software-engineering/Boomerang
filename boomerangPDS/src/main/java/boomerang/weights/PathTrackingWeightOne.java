/**
 * ***************************************************************************** 
 * Copyright (c) 2018 Fraunhofer IEM, Paderborn, Germany
 * <p>
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * <p>
 * SPDX-License-Identifier: EPL-2.0
 * <p>
 * Contributors:
 *   Johannes Spaeth - initial API and implementation
 * *****************************************************************************
 */
package boomerang.weights;

import boomerang.scope.ControlFlowGraph.Edge;
import boomerang.scope.Val;
import java.util.LinkedHashSet;
import java.util.Set;
import org.jspecify.annotations.NonNull;
import sync.pds.solver.nodes.Node;
import wpds.impl.Weight;

public class PathTrackingWeightOne implements PathTrackingWeight {

  @NonNull private static final PathTrackingWeightOne one = new PathTrackingWeightOne();

  private PathTrackingWeightOne() {
    /*  Singleton */
  }

  public static PathTrackingWeightOne one() {
    return one;
  }

  @NonNull
  @Override
  public Set<LinkedHashSet<Node<Edge, Val>>> getAllPathWitness() {
    throw new IllegalStateException("don't!");
  }

  @NonNull
  @Override
  public LinkedHashSet<Node<Edge, Val>> getShortestPathWitness() {
    throw new IllegalStateException("don't!");
  }

  @NonNull
  @Override
  public Weight extendWith(Weight o) {
    if (!(o instanceof PathTrackingWeight))
      throw new RuntimeException("Cannot extend to different types of weight!");
    PathTrackingWeight other = (PathTrackingWeight) o;
    LinkedHashSet<Node<Edge, Val>> shortestPathWitness = getShortestPathWitness();
    Set<Node<Edge, Val>> otherShortestPathWitness = other.getShortestPathWitness();
    LinkedHashSet<Node<Edge, Val>> newAllStatements =
        new LinkedHashSet<>(otherShortestPathWitness.size() + shortestPathWitness.size());
    newAllStatements.addAll(shortestPathWitness);
    newAllStatements.addAll(otherShortestPathWitness);

    Set<LinkedHashSet<Node<Edge, Val>>> allPathWitness = getAllPathWitness();
    Set<LinkedHashSet<Node<Edge, Val>>> otherAllPathWitness = other.getAllPathWitness();
    Set<LinkedHashSet<Node<Edge, Val>>> newAllPathStatements =
        new LinkedHashSet<>(allPathWitness.size() * otherAllPathWitness.size());

    for (LinkedHashSet<Node<Edge, Val>> pathPrefix : allPathWitness) {
      for (LinkedHashSet<Node<Edge, Val>> pathSuffix : otherAllPathWitness) {
        LinkedHashSet<Node<Edge, Val>> combinedPath =
            new LinkedHashSet<>(pathPrefix.size() + pathSuffix.size());
        combinedPath.addAll(pathPrefix);
        combinedPath.addAll(pathSuffix);
        newAllPathStatements.add(combinedPath);
      }
    }

    for (LinkedHashSet<Node<Edge, Val>> pathSuffix : otherAllPathWitness) {
      // TODO: [ms] check: do we have to copy or can we just point to it?
      LinkedHashSet<Node<Edge, Val>> combinedPath = new LinkedHashSet<>(pathSuffix);
      newAllPathStatements.add(combinedPath);
    }

    return new PathTrackingWeightImpl(newAllStatements, newAllPathStatements);
  }

  @NonNull
  @Override
  public Weight combineWith(Weight o) {
    if (!(o instanceof PathTrackingWeight))
      throw new RuntimeException("Cannot extend to different types of weight!");
    PathTrackingWeight other = (PathTrackingWeight) o;
    Set<LinkedHashSet<Node<Edge, Val>>> allPathWitness = getAllPathWitness();
    Set<LinkedHashSet<Node<Edge, Val>>> otherAllPathWitness = other.getAllPathWitness();
    Set<LinkedHashSet<Node<Edge, Val>>> newAllPathStatements =
        new LinkedHashSet<>(allPathWitness.size() * otherAllPathWitness.size());

    for (LinkedHashSet<Node<Edge, Val>> pathPrefix : allPathWitness) {
      // TODO: [ms] check: do we have to copy or can we just point to it?
      LinkedHashSet<Node<Edge, Val>> combinedPath = new LinkedHashSet<>(pathPrefix);
      newAllPathStatements.add(combinedPath);
    }
    for (LinkedHashSet<Node<Edge, Val>> pathPrefix : otherAllPathWitness) {
      // TODO: [ms] check: do we have to copy or can we just point to it?
      LinkedHashSet<Node<Edge, Val>> combinedPath = new LinkedHashSet<>(pathPrefix);
      newAllPathStatements.add(combinedPath);
    }

    LinkedHashSet<Node<Edge, Val>> shortestPathWitness = getShortestPathWitness();
    Set<Node<Edge, Val>> otherShortestPathWitness = other.getShortestPathWitness();
    if (shortestPathWitness.size() > otherShortestPathWitness.size()) {
      return new PathTrackingWeightImpl(
          new LinkedHashSet<>(otherShortestPathWitness), newAllPathStatements);
    }

    return new PathTrackingWeightImpl(
        new LinkedHashSet<>(shortestPathWitness), newAllPathStatements);
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
