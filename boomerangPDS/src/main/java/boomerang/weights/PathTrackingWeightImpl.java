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

  private PathTrackingWeightImpl(LinkedHashSet<Node<Edge, Val>> allStatement) {
    this.shortestPathWitness = allStatement;
  }

  public PathTrackingWeightImpl(Node<Edge, Val> relevantStatement) {
    this.shortestPathWitness = new LinkedHashSet<>();
    this.shortestPathWitness.add(relevantStatement);
  }

  @NonNull
  @Override
  public Weight extendWith(@NonNull Weight o) {
    // FIXME: [ms] something is weird with the field --> check again!
    if (!(o instanceof PathTrackingWeight)) {
      throw new RuntimeException("Cannot extend to different types of weight!");
    }

    PathTrackingWeightImpl other = (PathTrackingWeightImpl) o;
    LinkedHashSet<Node<Edge, Val>> newAllStatements = new LinkedHashSet<>();
    newAllStatements.addAll(shortestPathWitness);
    newAllStatements.addAll(other.shortestPathWitness);

    return new PathTrackingWeightImpl(newAllStatements);
  }

  @NonNull
  @Override
  public Weight combineWith(@NonNull Weight o) {
    if (!(o instanceof PathTrackingWeight)) {
      throw new RuntimeException("Cannot extend to different types of weight!");
    }
    PathTrackingWeightImpl other = (PathTrackingWeightImpl) o;

    if (shortestPathWitness.size() > other.shortestPathWitness.size()) {
      return new PathTrackingWeightImpl(new LinkedHashSet<>(other.shortestPathWitness));
    }

    return new PathTrackingWeightImpl(new LinkedHashSet<>(this.shortestPathWitness));
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
