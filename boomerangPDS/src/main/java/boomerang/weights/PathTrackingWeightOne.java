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
  public LinkedHashSet<Node<Edge, Val>> getShortestPathWitness() {
    throw new IllegalStateException("don't!");
  }

  @Override
  @NonNull
  public Weight extendWith(@NonNull Weight o) {
    throw new IllegalStateException("This should not happen!");
  }

  @Override
  @NonNull
  public Weight combineWith(@NonNull Weight o) {
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
