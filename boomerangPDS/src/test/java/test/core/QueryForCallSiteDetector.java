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

import boomerang.BackwardQuery;
import boomerang.Query;
import boomerang.scope.AccessPathParser;
import boomerang.scope.AnalysisScope;
import boomerang.scope.ControlFlowGraph.Edge;
import boomerang.scope.FrameworkScope;
import boomerang.scope.InvokeExpr;
import boomerang.scope.Val;
import boomerang.util.AccessPath;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class QueryForCallSiteDetector extends AnalysisScope {

  boolean resultsMustNotBeEmpty = false;
  boolean accessPathQuery = false;
  boolean integerQueries;
  Set<AccessPath> expectedAccessPaths = new HashSet<>();

  public QueryForCallSiteDetector(FrameworkScope frameworkScope) {
    super(frameworkScope);
  }

  private void getAllExpectedAccessPath(Edge u) {
    Val arg = u.getStart().getInvokeExpr().getArg(1);
    if (arg.isStringConstant()) {
      String value = arg.getStringValue();
      expectedAccessPaths.addAll(AccessPathParser.parseAllFromString(value, u.getMethod()));
    }
  }

  private class FirstArgumentOf implements ValueOfInterestInUnit {

    private final String methodNameMatcher;

    public FirstArgumentOf(String methodNameMatcher) {
      this.methodNameMatcher = methodNameMatcher;
    }

    @Override
    public Optional<? extends Query> test(Edge stmt) {
      if (!(stmt.getTarget().containsInvokeExpr())) return Optional.empty();
      InvokeExpr invokeExpr = stmt.getTarget().getInvokeExpr();
      if (!invokeExpr.getDeclaredMethod().getName().matches(methodNameMatcher))
        return Optional.empty();
      Val param = invokeExpr.getArg(0);
      if (!param.isLocal()) return Optional.empty();
      BackwardQuery newBackwardQuery = BackwardQuery.make(stmt, param);
      return Optional.<Query>of(newBackwardQuery);
    }
  }

  @Override
  protected Collection<? extends Query> generate(Edge stmt) {
    Optional<? extends Query> query = new FirstArgumentOf("queryFor").test(stmt);

    if (query.isPresent()) {
      return Collections.singleton(query.get());
    }
    query = new FirstArgumentOf("queryForAndNotEmpty").test(stmt);

    if (query.isPresent()) {
      resultsMustNotBeEmpty = true;
      return Collections.singleton(query.get());
    }
    query = new FirstArgumentOf("intQueryFor").test(stmt);
    if (query.isPresent()) {
      integerQueries = true;
      return Collections.singleton(query.get());
    }

    query = new FirstArgumentOf("accessPathQueryFor").test(stmt);
    if (query.isPresent()) {
      accessPathQuery = true;
      getAllExpectedAccessPath(stmt);
      return Collections.singleton(query.get());
    }
    return Collections.emptySet();
  }
}
