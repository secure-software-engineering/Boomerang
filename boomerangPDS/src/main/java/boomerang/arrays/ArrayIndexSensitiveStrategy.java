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
package boomerang.arrays;

import boomerang.scope.ControlFlowGraph.Edge;
import boomerang.scope.Field;
import boomerang.scope.Pair;
import boomerang.scope.Val;
import java.util.Set;
import sync.pds.solver.SyncPDSSolver.PDSSystem;
import sync.pds.solver.nodes.PushNode;
import wpds.interfaces.State;

public class ArrayIndexSensitiveStrategy implements ArrayHandlingStrategy {

  @Override
  public void handleForward(Edge curr, Pair<Val, Integer> arrayBase, Set<State> out) {
    out.add(
        new PushNode<>(curr, arrayBase.getX(), Field.array(arrayBase.getY()), PDSSystem.FIELDS));
  }

  @Override
  public void handleBackward(Edge curr, Pair<Val, Integer> arrayBase, Set<State> out) {
    out.add(
        new PushNode<>(curr, arrayBase.getX(), Field.array(arrayBase.getY()), PDSSystem.FIELDS));
  }
}
