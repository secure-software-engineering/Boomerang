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
package boomerang;

import boomerang.scope.AllocVal;
import boomerang.scope.ControlFlowGraph.Edge;
import java.util.Objects;
import wpds.impl.Weight;

public class WeightedForwardQuery<W extends Weight> extends ForwardQuery {

  private final W weight;

  public WeightedForwardQuery(Edge stmt, AllocVal variable, W weight) {
    super(stmt, variable);
    this.weight = weight;
  }

  public W weight() {
    return weight;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    WeightedForwardQuery<?> that = (WeightedForwardQuery<?>) o;
    return Objects.equals(weight, that.weight);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), weight);
  }

  @Override
  public String toString() {
    return "Weighted Forward Query: " + getAllocVal() + ", " + weight;
  }
}
