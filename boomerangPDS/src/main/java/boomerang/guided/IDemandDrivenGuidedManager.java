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
package boomerang.guided;

import boomerang.BackwardQuery;
import boomerang.ForwardQuery;
import boomerang.Query;
import boomerang.scope.ControlFlowGraph.Edge;
import boomerang.scope.Val;
import java.util.Collection;

public interface IDemandDrivenGuidedManager {
  Collection<Query> onForwardFlow(ForwardQuery query, Edge dataFlowEdge, Val dataFlowVal);

  Collection<Query> onBackwardFlow(BackwardQuery query, Edge dataFlowEdge, Val dataFlowVal);
}
