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
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import org.jspecify.annotations.NonNull;
import sync.pds.solver.nodes.Node;
import wpds.impl.Weight;

public class PathTrackingWeightImpl implements PathTrackingWeight {

  /**
   * This set keeps track of all statements on a shortest path that use an alias from source to
   * sink.
   */
  @NonNull private final LinkedHashSet<Node<Edge, Val>> shortestPathWitness;

  /**
   * This set keeps track of all statement along all paths that use an alias from source to sink.
   */
  @NonNull private final Set<LinkedHashSet<Node<Edge, Val>>> allPathWitness;

  public PathTrackingWeightImpl(Node<Edge, Val> relevantStatement) {
    this.shortestPathWitness = new LinkedHashSet<>();
    this.shortestPathWitness.add(relevantStatement);
    LinkedHashSet<Node<Edge, Val>> firstDataFlowPath = new LinkedHashSet<>();
    firstDataFlowPath.add(relevantStatement);
    this.allPathWitness = new LinkedHashSet<>();
    this.allPathWitness.add(firstDataFlowPath);
  }

  public PathTrackingWeightImpl(
      LinkedHashSet<Node<Edge, Val>> allStatement,
      Set<LinkedHashSet<Node<Edge, Val>>> allPathWitness) {
    this.shortestPathWitness = allStatement;
    this.allPathWitness = allPathWitness;
  }

  @NonNull
  @Override
  public Set<LinkedHashSet<Node<Edge, Val>>> getAllPathWitness() {
    return allPathWitness;
  }

  @NonNull
  @Override
  public Weight extendWith(Weight o) {
    if (!(o instanceof PathTrackingWeight))
      throw new RuntimeException("Cannot extend to different types of weight!");
    PathTrackingWeight other = (PathTrackingWeight) o;
    LinkedHashSet<Node<Edge, Val>> newAllStatements = new LinkedHashSet<>();
    newAllStatements.addAll(shortestPathWitness);
    newAllStatements.addAll(other.getShortestPathWitness());

    Set<LinkedHashSet<Node<Edge, Val>>> newAllPathStatements = new LinkedHashSet<>();
    for (LinkedHashSet<Node<Edge, Val>> pathPrefix : allPathWitness) {
      for (LinkedHashSet<Node<Edge, Val>> pathSuffix : other.getAllPathWitness()) {
        LinkedHashSet<Node<Edge, Val>> combinedPath = new LinkedHashSet<>();
        combinedPath.addAll(pathPrefix);
        combinedPath.addAll(pathSuffix);
        newAllPathStatements.add(combinedPath);
      }
    }
    if (allPathWitness.isEmpty()) {
      for (LinkedHashSet<Node<Edge, Val>> pathSuffix : other.getAllPathWitness()) {
        LinkedHashSet<Node<Edge, Val>> combinedPath = new LinkedHashSet<>(pathSuffix);
        newAllPathStatements.add(combinedPath);
      }
    }
    if (other.getAllPathWitness().isEmpty()) {
      for (LinkedHashSet<Node<Edge, Val>> pathSuffix : allPathWitness) {
        LinkedHashSet<Node<Edge, Val>> combinedPath = new LinkedHashSet<>(pathSuffix);
        newAllPathStatements.add(combinedPath);
      }
    }

    return new PathTrackingWeightImpl(newAllStatements, newAllPathStatements);
  }

  @NonNull
  @Override
  public Weight combineWith(Weight o) {
    if (!(o instanceof PathTrackingWeight))
      throw new RuntimeException("Cannot extend to different types of weight!");
    PathTrackingWeight other = (PathTrackingWeight) o;
    Set<LinkedHashSet<Node<Edge, Val>>> newAllPathStatements = new LinkedHashSet<>();
    for (LinkedHashSet<Node<Edge, Val>> pathPrefix : allPathWitness) {
      // TODO: [ms] check: do we have to copy or can we just point to it?
      LinkedHashSet<Node<Edge, Val>> combinedPath = new LinkedHashSet<>(pathPrefix);
      newAllPathStatements.add(combinedPath);
    }
    for (LinkedHashSet<Node<Edge, Val>> pathPrefix : other.getAllPathWitness()) {
      // TODO: [ms] check: do we have to copy or can we just point to it?
      LinkedHashSet<Node<Edge, Val>> combinedPath = new LinkedHashSet<>(pathPrefix);
      newAllPathStatements.add(combinedPath);
    }

    if (shortestPathWitness.size() > other.getShortestPathWitness().size()) {
      return new PathTrackingWeightImpl(
          new LinkedHashSet<>(other.getShortestPathWitness()), newAllPathStatements);
    }

    return new PathTrackingWeightImpl(
        new LinkedHashSet<>(this.shortestPathWitness), newAllPathStatements);
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

  @NonNull
  public Set<Node<Edge, Val>> getShortestPathWitness() {
    return Collections.unmodifiableSet(shortestPathWitness);
  }
}
