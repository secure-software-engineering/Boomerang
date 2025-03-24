/**
 * ***************************************************************************** 
 * Copyright (c) 2025 Fraunhofer IEM, Paderborn, Germany. This program and the
 * accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at http://www.eclipse.org/legal/epl-2.0.
 *
 * <p>SPDX-License-Identifier: EPL-2.0
 *
 * <p>Contributors: Johannes Spaeth - initial API and implementation
 * *****************************************************************************
 */
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
    if (!(o instanceof PathTrackingWeightImpl)) {
      throw new RuntimeException("Cannot extend to different types of weight!");
    }
    return new PathTrackingWeightImpl(new LinkedHashSet<>());
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