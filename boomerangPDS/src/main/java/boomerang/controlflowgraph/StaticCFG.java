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

import boomerang.options.BoomerangOptions;
import boomerang.scope.Method;
import boomerang.scope.Statement;
import boomerang.scope.Val;
import sparse.SparsificationStrategy;

public class StaticCFG implements ObservableControlFlowGraph {

  private final SparsificationStrategy<? extends Method, ? extends Statement>
      sparsificationStrategy;

  private final BoomerangOptions options;

  private Val currentVal;

  public void setCurrentVal(Val val) {
    this.currentVal = val;
  }

  public StaticCFG(BoomerangOptions options) {
    this.options = options;
    this.sparsificationStrategy = options.getSparsificationStrategy();
  }

  @Override
  public void addPredsOfListener(PredecessorListener l) {
    for (Statement s : l.getCurr().getMethod().getControlFlowGraph().getPredsOf(l.getCurr())) {
      l.getPredecessor(s);
    }
  }

  @Override
  public void addSuccsOfListener(SuccessorListener l) {
    Method method = l.getCurr().getMethod();
    Statement curr = l.getCurr();
    /*
    TODO: [ms] reenable sparsification
     if (sparsificationStrategy != SparsificationStrategy.NONE) {
      SparseAliasingCFG sparseCFG = getSparseCFG(method, curr, currentVal);
      if (sparseCFG != null) {
        propagateSparse(l, method, curr, sparseCFG);
      } else if (options.handleSpecialInvokeAsNormalPropagation()) {
        propagateDefault(l);
      } else {
        propagateDefault(l); // back up when not found
      }
    } else */
    {
      propagateDefault(l);
    }
  }

  /*
  TODO: [ms] reenable sparsification
  private void propagateSparse(
      SuccessorListener l, Method method, Statement curr, SparseAliasingCFG sparseCFG) {
    Set<Unit> successors = sparseCFG.getGraph().successors(SootAdapter.asStmt(curr));
    for (Unit succ : successors) {
      sparsificationStrategy.getCounter().countForward();
      l.getSuccessor(SootAdapter.asStatement(succ, method));
    }
  }
  */

  private void propagateDefault(SuccessorListener l) {
    for (Statement s : l.getCurr().getMethod().getControlFlowGraph().getSuccsOf(l.getCurr())) {
      sparsificationStrategy.getCounter().countForwardPropagation();
      l.getSuccessor(s);
    }
  }

  /*
  TODO: [ms] reenable sparsification
  private SparseAliasingCFG getSparseCFG(Method method, Statement stmt, Val currentVal) {
    SootMethod sootMethod = ((JimpleMethod) method).getDelegate();
    Stmt sootStmt = ((JimpleStatement) stmt).getDelegate();
    SparseCFGCache sparseCFGCache =
        SparseCFGCache.getInstance(
            sparsificationStrategy, options.ignoreSparsificationAfterQuery());
    SparseAliasingCFG sparseCFG =
        sparseCFGCache.getSparseCFGForForwardPropagation(sootMethod, sootStmt, currentVal);
    return sparseCFG;
  }
  */

  @Override
  public void step(Statement curr, Statement succ) {}

  @Override
  public void unregisterAllListeners() {}
}
