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
package boomerang.sparse;

import boomerang.scope.Method;
import boomerang.scope.Statement;
import boomerang.scope.Val;
import boomerang.sparse.eval.PropagationCounter;
import boomerang.sparse.eval.SparseCFGQueryLog;
import java.util.List;
import org.jspecify.annotations.Nullable;

/**
 * Builds and stores the {@link SparseAliasingCFG}s of one analysis instance. If a lookup returns
 * null or a graph that does not contain the requested statement, the solvers fall back to the
 * original control flow graph.
 */
public interface SparseCFGCache {

  /**
   * Retrieves a {@link SparseAliasingCFG} that was built by a backward query and contains the given
   * statement.
   *
   * @param method the method that contains the statement
   * @param stmt the statement whose successors are requested
   * @param val the propagated value, if known
   * @return the sparse graph or null if no graph is available
   */
  @Nullable SparseAliasingCFG getSparseCFGForForwardPropagation(
      Method method, Statement stmt, @Nullable Val val);

  /**
   * Retrieves or builds the {@link SparseAliasingCFG} for a backward propagation.
   *
   * @param initialQueryVal the variable of the backward query
   * @param initialQueryStmt the statement of the backward query
   * @param currentMethod the method the propagation is currently in
   * @param currentVal the currently propagated value
   * @param currentStmt the statement whose predecessors are requested
   * @return the sparse graph or null if no graph is available
   */
  @Nullable SparseAliasingCFG getSparseCFGForBackwardPropagation(
      Val initialQueryVal,
      Statement initialQueryStmt,
      Method currentMethod,
      Val currentVal,
      Statement currentStmt);

  /**
   * @return a log entry for each sparse graph that was built
   */
  List<SparseCFGQueryLog> getQueryLogs();

  /**
   * @return the counter for the propagations of this analysis instance
   */
  PropagationCounter getPropagationCounter();
}
