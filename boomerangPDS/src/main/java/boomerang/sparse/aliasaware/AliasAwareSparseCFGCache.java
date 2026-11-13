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
package boomerang.sparse.aliasaware;

import boomerang.scope.Method;
import boomerang.scope.Statement;
import boomerang.scope.Val;
import boomerang.sparse.AbstractSparseCFGCache;
import boomerang.sparse.SparseAliasingCFG;
import boomerang.sparse.eval.SparseCFGQueryLog;
import java.util.Collection;
import org.jspecify.annotations.Nullable;

/** Cache for the graphs of the {@link AliasAwareSparsificationStrategy}. */
public class AliasAwareSparseCFGCache extends AbstractSparseCFGCache {

  private final boolean ignoreAfterQuery;

  public AliasAwareSparseCFGCache(boolean ignoreAfterQuery) {
    this.ignoreAfterQuery = ignoreAfterQuery;
  }

  @Override
  protected @Nullable SparseAliasingCFG build(
      Val initialQueryVal,
      Statement initialQueryStmt,
      Method method,
      Collection<Seed> seeds,
      Collection<Statement> requiredStmts,
      SparseCFGQueryLog queryLog) {
    AliasAwareSparseCFGBuilder builder =
        new AliasAwareSparseCFGBuilder(method, requiredStmts, initialQueryVal, ignoreAfterQuery);
    Val lastVal = null;
    for (Seed seed : seeds) {
      if (!builder.addSeed(seed.val(), seed.stmt())) {
        return null;
      }
      lastVal = seed.val();
    }
    return builder.build(lastVal, queryLog);
  }
}
