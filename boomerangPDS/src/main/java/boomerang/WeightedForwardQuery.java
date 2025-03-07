/**
 * ***************************************************************************** Copyright (c) 2018
 * Fraunhofer IEM, Paderborn, Germany. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * <p>SPDX-License-Identifier: EPL-2.0
 *
 * <p>Contributors: Johannes Spaeth - initial API and implementation
 * *****************************************************************************
 */
package boomerang;

import boomerang.scope.AllocVal;
import boomerang.scope.ControlFlowGraph.Edge;
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
}
