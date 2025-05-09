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
import boomerang.guided.Specification.MethodWithSelector;
import boomerang.guided.Specification.Parameter;
import boomerang.guided.Specification.QueryDirection;
import boomerang.guided.Specification.QuerySelector;
import boomerang.scope.AllocVal;
import boomerang.scope.ControlFlowGraph.Edge;
import boomerang.scope.Method;
import boomerang.scope.Statement;
import boomerang.scope.Val;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class SimpleSpecificationGuidedManager implements IDemandDrivenGuidedManager {

  private final Specification spec;

  public SimpleSpecificationGuidedManager(Specification spec) {
    this.spec = spec;
  }

  @Override
  public Collection<Query> onForwardFlow(ForwardQuery query, Edge dataFlowEdge, Val dataFlowVal) {
    Statement stmt = dataFlowEdge.getStart();
    Set<Query> res = new LinkedHashSet<>();
    if (stmt.containsInvokeExpr()) {
      Set<MethodWithSelector> selectors =
          spec.getMethodAndQueries().stream()
              .filter(x -> isInOnList(x, stmt, dataFlowVal, QueryDirection.FORWARD))
              .collect(Collectors.toSet());
      for (MethodWithSelector sel : selectors) {
        res.addAll(createNewQueries(sel, stmt));
      }
    }
    return res;
  }

  @Override
  public Collection<Query> onBackwardFlow(BackwardQuery query, Edge dataFlowEdge, Val dataFlowVal) {
    Statement stmt = dataFlowEdge.getStart();
    Set<Query> res = new LinkedHashSet<>();
    if (stmt.containsInvokeExpr()) {
      Set<MethodWithSelector> selectors =
          spec.getMethodAndQueries().stream()
              .filter(x -> isInOnList(x, stmt, dataFlowVal, QueryDirection.BACKWARD))
              .collect(Collectors.toSet());
      for (MethodWithSelector sel : selectors) {
        res.addAll(createNewQueries(sel, stmt));
      }
    }
    return res;
  }

  private Collection<Query> createNewQueries(MethodWithSelector sel, Statement stmt) {
    Set<Query> results = new LinkedHashSet<>();
    Method method = stmt.getMethod();
    for (QuerySelector qSel : sel.getGo()) {
      Optional<Val> parameterVal = getParameterVal(stmt, qSel.argumentSelection);
      if (parameterVal.isPresent()) {
        if (qSel.direction == QueryDirection.BACKWARD) {
          for (Statement pred : method.getControlFlowGraph().getPredsOf(stmt)) {
            results.add(BackwardQuery.make(new Edge(pred, stmt), parameterVal.get()));
          }
        } else if (qSel.direction == QueryDirection.FORWARD) {
          for (Statement succ : method.getControlFlowGraph().getSuccsOf(stmt)) {
            results.add(
                new ForwardQuery(
                    new Edge(stmt, succ),
                    new AllocVal(parameterVal.get(), stmt, parameterVal.get())));
          }
        }
      }
    }
    return results;
  }

  public boolean isInOnList(
      MethodWithSelector methodSelector, Statement stmt, Val fact, QueryDirection direction) {
    if (stmt.getInvokeExpr()
        .getDeclaredMethod()
        .toMethodWrapper()
        .equals(methodSelector.getMethod())) {
      Collection<QuerySelector> on = methodSelector.getOn();
      return isInList(on, direction, stmt, fact);
    }

    return false;
  }

  private boolean isInList(
      Collection<QuerySelector> list, QueryDirection direction, Statement stmt, Val fact) {
    return list.stream()
        .anyMatch(
            sel -> (sel.direction == direction && isParameter(stmt, fact, sel.argumentSelection)));
  }

  private boolean isParameter(Statement stmt, Val fact, Parameter argumentSelection) {
    if (stmt.getInvokeExpr().isInstanceInvokeExpr() && argumentSelection.equals(Parameter.base())) {
      return stmt.getInvokeExpr().getBase().equals(fact);
    }
    if (argumentSelection.equals(Parameter.returnParam())) {
      return stmt.isAssignStmt() && stmt.getLeftOp().equals(fact);
    }
    return stmt.getInvokeExpr().getArgs().size() > argumentSelection.getValue()
        && argumentSelection.getValue() >= 0
        && stmt.getInvokeExpr().getArg(argumentSelection.getValue()).equals(fact);
  }

  private Optional<Val> getParameterVal(Statement stmt, Parameter selector) {
    if (stmt.containsInvokeExpr()
        && !stmt.getInvokeExpr().isStaticInvokeExpr()
        && selector.equals(Parameter.base())) {
      return Optional.of(stmt.getInvokeExpr().getBase());
    }
    if (stmt.isAssignStmt() && selector.equals(Parameter.returnParam())) {
      return Optional.of(stmt.getLeftOp());
    }
    if (stmt.getInvokeExpr().getArgs().size() > selector.getValue() && selector.getValue() >= 0) {
      return Optional.of(stmt.getInvokeExpr().getArg(selector.getValue()));
    }
    return Optional.empty();
  }
}
