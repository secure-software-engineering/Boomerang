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
import boomerang.scope.Type;
import boomerang.scope.Val;
import java.util.Optional;

class AllocationSiteOf implements ValueOfInterestInUnit {
  private final String type;

  public AllocationSiteOf(String type) {
    this.type = type;
  }

  public Optional<Query> test(Edge cfgEdge) {
    Statement stmt = cfgEdge.getStart();
    if (stmt.isAssignStmt()) {
      if (stmt.getLeftOp().isLocal() && stmt.getRightOp().isNewExpr()) {
        Type expr = stmt.getRightOp().getNewExprType();
        if (expr.isSubtypeOf(type)) {
          Val local = stmt.getLeftOp();
          ForwardQuery forwardQuery =
              new ForwardQuery(cfgEdge, new AllocVal(local, stmt, stmt.getRightOp()));
          return Optional.of(forwardQuery);
        }
      }
    }
    return Optional.empty();
  }
}
