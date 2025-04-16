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
package test.core;

import boomerang.BackwardQuery;
import boomerang.Query;
import boomerang.scope.ControlFlowGraph.Edge;
import boomerang.scope.InvokeExpr;
import boomerang.scope.Val;
import java.util.Optional;

public class FirstArgumentOf implements ValueOfInterestInUnit {

  private final String methodNameMatcher;

  public FirstArgumentOf(String methodNameMatcher) {
    this.methodNameMatcher = methodNameMatcher;
  }

  @Override
  public Optional<? extends Query> test(Edge stmt) {
    if (!(stmt.getStart().containsInvokeExpr())) return Optional.empty();
    InvokeExpr invokeExpr = stmt.getStart().getInvokeExpr();
    if (!invokeExpr.getDeclaredMethod().getName().matches(methodNameMatcher))
      return Optional.empty();
    Val param = invokeExpr.getArg(0);
    if (!param.isLocal()) return Optional.empty();
    BackwardQuery newBackwardQuery = BackwardQuery.make(stmt, param);
    return Optional.<Query>of(newBackwardQuery);
  }
}
