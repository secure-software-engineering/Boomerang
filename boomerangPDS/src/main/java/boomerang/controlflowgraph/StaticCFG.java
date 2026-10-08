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
package boomerang.controlflowgraph;

import boomerang.scope.Statement;
import boomerang.sparse.SparseAliasingCFG;
import boomerang.sparse.SparseCFGCache;
import java.util.Collection;

public class StaticCFG implements ObservableControlFlowGraph {

  private final SparseCFGCache sparseCFGCache;

  public StaticCFG(SparseCFGCache sparseCFGCache) {
    this.sparseCFGCache = sparseCFGCache;
  }

  @Override
  public void addPredsOfListener(PredecessorListener l) {
    for (Statement s : l.getCurr().getMethod().getControlFlowGraph().getPredsOf(l.getCurr())) {
      l.getPredecessor(s);
    }
  }

  @Override
  public void addSuccsOfListener(SuccessorListener l) {
    for (Statement s : getSuccessors(l)) {
      sparseCFGCache.getPropagationCounter().countForwardPropagation();
      l.getSuccessor(s);
    }
  }

  /**
   * Returns the successors in the sparse control flow graph if a sparse graph containing the
   * current statement is available and the successors in the original graph otherwise.
   */
  private Collection<Statement> getSuccessors(SuccessorListener l) {
    Statement curr = l.getCurr();
    SparseAliasingCFG sparseCFG =
        sparseCFGCache.getSparseCFGForForwardPropagation(curr.getMethod(), curr, l.getFact());
    if (sparseCFG != null && sparseCFG.contains(curr)) {
      return sparseCFG.successors(curr);
    }
    return curr.getSuccessors();
  }

  @Override
  public void step(Statement curr, Statement succ) {}

  @Override
  public void unregisterAllListeners() {}
}
