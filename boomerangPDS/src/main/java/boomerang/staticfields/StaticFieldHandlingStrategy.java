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
import boomerang.scope.StaticFieldVal;
import boomerang.scope.Val;
import java.util.Set;
import wpds.interfaces.State;

public interface StaticFieldHandlingStrategy {

  void handleForward(Edge storeStmt, Val storedVal, StaticFieldVal staticVal, Set<State> out);

  void handleBackward(Edge curr, Val leftOp, StaticFieldVal staticField, Set<State> out);
}
