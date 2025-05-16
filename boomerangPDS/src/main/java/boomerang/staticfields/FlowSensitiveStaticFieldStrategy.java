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
package boomerang.staticfields;

import boomerang.scope.ControlFlowGraph.Edge;
import boomerang.scope.IStaticFieldRef;
import boomerang.scope.StaticFieldVal;
import boomerang.scope.Val;
import java.util.Set;
import sync.pds.solver.nodes.Node;
import wpds.interfaces.State;

public class FlowSensitiveStaticFieldStrategy implements StaticFieldHandlingStrategy {
  @Override
  public void handleForward(
      Edge storeStmt, Val storedVal, StaticFieldVal staticVal, Set<State> out) {
    out.add(new Node<>(storeStmt, staticVal));
  }

  @Override
  public void handleBackward(
      Edge loadStatement, Val loadedVal, StaticFieldVal staticVal, Set<State> out) {
    IStaticFieldRef staticFieldRef = loadStatement.getStart().getStaticField();
    out.add(new Node<>(loadStatement, staticFieldRef.asStaticFieldVal()));
  }
}
