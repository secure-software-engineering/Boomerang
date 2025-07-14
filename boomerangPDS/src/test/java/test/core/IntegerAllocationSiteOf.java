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
package test.core;

import boomerang.ForwardQuery;
import boomerang.Query;
import boomerang.scope.AllocVal;
import boomerang.scope.ControlFlowGraph.Edge;
import boomerang.scope.Statement;
import java.util.Optional;

class IntegerAllocationSiteOf implements ValueOfInterestInUnit {
  public Optional<? extends Query> test(Edge cfgEdge) {
    Statement stmt = cfgEdge.getStart();
    if (stmt.isAssignStmt()) {
      if (stmt.getLeftOp().toString().contains("allocation")) {
        if (stmt.getLeftOp().isLocal() && stmt.getRightOp().isIntConstant()) {
          AllocVal allocVal = new AllocVal(stmt.getLeftOp(), stmt, stmt.getRightOp());
          ForwardQuery forwardQuery = new ForwardQuery(cfgEdge, allocVal);
          return Optional.of(forwardQuery);
        }
      }
    }

    return Optional.empty();
  }
}
