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

import boomerang.options.BoomerangOptions;
import boomerang.scope.Method;
import boomerang.scope.Statement;
import boomerang.scope.Val;
import boomerang.sparse.eval.PropagationCounter;
import boomerang.sparse.eval.SparseCFGQueryLog;
import java.util.Collections;
import java.util.List;
import org.jspecify.annotations.Nullable;

/** Strategy that disables sparsification, see {@link SparsificationStrategy#NONE}. */
public class NoSparsificationStrategy implements SparsificationStrategy {

  @Override
  public SparseCFGCache createCache(BoomerangOptions options) {
    return new NoSparseCFGCache();
  }

  @Override
  public String getName() {
    return "none";
  }

  @Override
  public String toString() {
    return getName();
  }

  /** Cache without sparse control flow graphs: the solvers use the original ones. */
  private static class NoSparseCFGCache implements SparseCFGCache {

    private final PropagationCounter counter = new PropagationCounter();

    @Override
    public @Nullable SparseAliasingCFG getSparseCFGForForwardPropagation(
        Method method, Statement stmt, @Nullable Val val) {
      return null;
    }

    @Override
    public @Nullable SparseAliasingCFG getSparseCFGForBackwardPropagation(
        Val initialQueryVal,
        Statement initialQueryStmt,
        Method currentMethod,
        Val currentVal,
        Statement currentStmt) {
      return null;
    }

    @Override
    public List<SparseCFGQueryLog> getQueryLogs() {
      return Collections.emptyList();
    }

    @Override
    public PropagationCounter getPropagationCounter() {
      return counter;
    }
  }
}
