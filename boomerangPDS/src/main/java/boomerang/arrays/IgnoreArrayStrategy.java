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
import boomerang.scope.IArrayRef;
import java.util.Set;
import wpds.interfaces.State;

public class IgnoreArrayStrategy implements ArrayHandlingStrategy {

  @Override
  public void handleForward(Edge storeStmt, IArrayRef storedVal, Set<State> out) {}

  @Override
  public void handleBackward(Edge curr, IArrayRef arrayBase, Set<State> out) {}
}
