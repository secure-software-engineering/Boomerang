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
package boomerang.controlflowgraph;

import boomerang.scope.Statement;

public interface ObservableControlFlowGraph {

  void addPredsOfListener(PredecessorListener l);

  void addSuccsOfListener(SuccessorListener l);

  void step(Statement curr, Statement succ);

  void unregisterAllListeners();
}
