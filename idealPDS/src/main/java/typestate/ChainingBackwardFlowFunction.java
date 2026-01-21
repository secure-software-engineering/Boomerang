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

import boomerang.flowfunction.DefaultBackwardFlowFunction;
import boomerang.options.IAllocationSite;
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

public class ChainingBackwardFlowFunction extends DefaultBackwardFlowFunction {

  private final Collection<MethodWrapper> methodChains;

  public ChainingBackwardFlowFunction(
      IAllocationSite allocationSite,
      Strategies strategies,
      Collection<MethodWrapper> methodChains) {
    super(allocationSite, strategies);

    this.methodChains = methodChains;
  }

  @Override
  public Collection<State> callToReturnFlow(
      ControlFlowGraph.Edge currEdge, ControlFlowGraph.Edge nextEdge, Val fact) {
    Collection<State> out = new LinkedHashSet<>(super.callToReturnFlow(currEdge, nextEdge, fact));

    Statement statement = nextEdge.getTarget();
    if (statement.isAssignStmt() && statement.containsInvokeExpr()) {
      InvokeExpr invokeExpr = statement.getInvokeExpr();
      DeclaredMethod declaredMethod = invokeExpr.getDeclaredMethod();

      if (methodChains.contains(declaredMethod.toMethodWrapper())) {
        Val leftOp = statement.getLeftOp();

        if (leftOp.equals(fact) && invokeExpr.isInstanceInvokeExpr()) {
          out.add(new Node<>(nextEdge, invokeExpr.getBase()));
        }
      }
    }

    return out;
  }
}
