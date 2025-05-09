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

import boomerang.scope.Statement;
import boomerang.scope.Val;
import sync.pds.solver.WeightFunctions;
import sync.pds.solver.nodes.Node;

public class MinDistanceWeightFunctions
    implements WeightFunctions<Statement, Val, Statement, MinDistanceWeight> {

  @Override
  public MinDistanceWeight push(
      Node<Statement, Val> curr, Node<Statement, Val> succ, Statement callSite) {
    if (!curr.fact().isStatic()) {
      return new MinDistanceWeightImpl(1);
    }
    return MinDistanceWeightOne.one();
  }

  @Override
  public MinDistanceWeight normal(Node<Statement, Val> curr, Node<Statement, Val> succ) {
    if (!curr.fact().equals(succ.fact())) {
      return new MinDistanceWeightImpl(1);
    }
    if (succ.stmt().containsInvokeExpr() && succ.stmt().uses(curr.fact())) {
      return new MinDistanceWeightImpl(1);
    }
    return MinDistanceWeightOne.one();
  }

  @Override
  public MinDistanceWeight pop(Node<Statement, Val> curr) {
    return MinDistanceWeightOne.one();
  }

  @Override
  public MinDistanceWeight getOne() {
    return MinDistanceWeightOne.one();
  }
}
