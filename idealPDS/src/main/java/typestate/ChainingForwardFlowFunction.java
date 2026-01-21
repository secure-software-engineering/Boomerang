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
package typestate;

import boomerang.ForwardQuery;
import boomerang.flowfunction.DefaultForwardFlowFunction;
import boomerang.scope.ControlFlowGraph;
import boomerang.scope.DeclaredMethod;
import boomerang.scope.InvokeExpr;
import boomerang.scope.Statement;
import boomerang.scope.Val;
import boomerang.solver.Strategies;
import boomerang.utils.MethodWrapper;
import java.util.Collection;
import java.util.LinkedHashSet;
import sync.pds.solver.nodes.Node;
import wpds.interfaces.State;

public class ChainingForwardFlowFunction extends DefaultForwardFlowFunction {

  private final Collection<MethodWrapper> methodChains;

  public ChainingForwardFlowFunction(
      Strategies strategies, Collection<MethodWrapper> methodChains) {
    super(strategies);

    this.methodChains = methodChains;
  }

  @Override
  public Collection<State> callToReturnFlow(
      ForwardQuery query, ControlFlowGraph.Edge edge, Val fact) {
    Collection<State> out = new LinkedHashSet<>(super.callToReturnFlow(query, edge, fact));

    Statement statement = edge.getStart();
    if (statement.isAssignStmt() && statement.containsInvokeExpr()) {
      InvokeExpr invokeExpr = statement.getInvokeExpr();
      DeclaredMethod declaredMethod = invokeExpr.getDeclaredMethod();

      if (methodChains.contains(declaredMethod.toMethodWrapper())) {
        /* If the current statement is a chained method call, consider it as an alias and
         * continue the propagation with the implicitly defined local
         */
        if (invokeExpr.isInstanceInvokeExpr()) {
          Val base = invokeExpr.getBase();

          if (base.equals(fact)) {
            out.add(new Node<>(edge, statement.getLeftOp()));
          }
        }
      }
    }

    return out;
  }
}
