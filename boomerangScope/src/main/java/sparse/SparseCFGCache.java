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
package sparse;

import boomerang.scope.Val;
import java.util.List;
import sparse.eval.SparseCFGQueryLog;

public interface SparseCFGCache<M, S> {

  static SparseCFGCache getInstance(SparsificationStrategy strategy, boolean ignoreAfterQuery) {

    // FIXME: [ms] fix/refactor mapping
    /*
    switch (strategy) {
      case TYPE_BASED:
        return TypeBasedSparseCFGCache.getInstance();
      case ALIAS_AWARE:
        return AliasAwareSparseCFGCache.getInstance(ignoreAfterQuery);
    }
    */
    throw new RuntimeException("SparsificationStrategy not implemented");
  }

  /**
   * For retrieving the same {@link SparseAliasingCFG} built by the backward query
   *
   * @param m
   * @param stmt
   * @return
   */
  SparseAliasingCFG getSparseCFGForForwardPropagation(M m, S stmt, Val val);

  /**
   * For building the {@link SparseAliasingCFG} for the first time for a backward query.
   *
   * @param initialQueryVal
   * @param initialQueryStmt
   * @param currentMethod
   * @param currentVal
   * @param currentStmt
   * @return
   */
  SparseAliasingCFG getSparseCFGForBackwardPropagation(
      Val initialQueryVal, S initialQueryStmt, M currentMethod, Val currentVal, S currentStmt);

  List<SparseCFGQueryLog> getQueryLogs();
}
