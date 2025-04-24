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

import boomerang.scope.ControlFlowGraph;
import boomerang.scope.Statement;
import boomerang.scope.Val;
import java.util.Map;
import java.util.Set;
import org.jspecify.annotations.NonNull;
import sync.pds.solver.nodes.Node;
import wpds.impl.Weight;

public interface DataFlowPathWeight extends Weight {
  @NonNull PathTrackingWeight getPath();

  @NonNull PathConditionWeight getCondition();

  @NonNull Set<Node<ControlFlowGraph.Edge, Val>> getAllStatements();

  @NonNull Map<Statement, PathConditionWeightImpl.ConditionDomain> getConditions();

  @NonNull Map<Val, PathConditionWeightImpl.ConditionDomain> getEvaluationMap();
}
