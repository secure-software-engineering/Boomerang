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
package boomerang.sparse.typebased;

import boomerang.scope.Method;
import boomerang.scope.Statement;
import boomerang.scope.Type;
import boomerang.scope.Val;
import boomerang.sparse.AbstractSparseCFGCache;
import boomerang.sparse.SparseAliasingCFG;
import boomerang.sparse.eval.SparseCFGQueryLog;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.jspecify.annotations.Nullable;

/**
 * Cache for the graphs of the {@link TypeBasedSparsificationStrategy}. The container types are
 * found per method, but are relevant in all methods: e.g. a field of the query type may be written
 * in a method that only accesses the container. Thus, the tracked types are shared by all graphs of
 * a query, and the types of the seed values are tracked as well.
 */
public class TypeBasedSparseCFGCache extends AbstractSparseCFGCache {

  private record Query(Val val, Statement stmt) {}

  private final Map<Query, Set<Type>> trackedTypesPerQuery = new HashMap<>();

  @Override
  protected @Nullable SparseAliasingCFG build(
      Val initialQueryVal,
      Statement initialQueryStmt,
      Method method,
      Collection<Seed> seeds,
      Collection<Statement> requiredStmts,
      SparseCFGQueryLog queryLog) {
    Set<Type> trackedTypes =
        trackedTypesPerQuery.computeIfAbsent(
            new Query(initialQueryVal, initialQueryStmt), q -> new LinkedHashSet<>());
    trackedTypes.add(initialQueryVal.getType());
    for (Seed seed : seeds) {
      trackedTypes.add(seed.val().getType());
    }

    List<Statement> seedStmts = seeds.stream().map(Seed::stmt).toList();
    TypeBasedSparseCFGBuilder builder = new TypeBasedSparseCFGBuilder(method, requiredStmts);
    SparseAliasingCFG cfg = builder.build(initialQueryVal, trackedTypes, seedStmts, queryLog);
    if (cfg != null) {
      trackedTypes.addAll(builder.getTrackedTypes());
    }
    return cfg;
  }
}
