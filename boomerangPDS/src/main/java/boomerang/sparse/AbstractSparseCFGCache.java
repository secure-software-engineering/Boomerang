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
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.jspecify.annotations.Nullable;

/**
 * Caches one sparse graph per method and backward query. The graph is built for the seeds, i.e. the
 * pairs of value and statement at which the backward propagation requested the graph. If the
 * propagation reaches a statement that is not part of the graph or a value that the graph does not
 * {@link SparseAliasingCFG#tracks(Val) track}, the pair is added as seed and the graph is rebuilt.
 * Since the kept statements of a rebuilt graph are a superset of the previous ones, the
 * propagations along the previous graph remain valid.
 */
public abstract class AbstractSparseCFGCache implements SparseCFGCache {

  /** A value and the statement at which its propagation requested a sparse graph */
  public record Seed(Val val, Statement stmt) {}

  private record Key(Method method, Val initialQueryVal, Statement initialQueryStmt) {}

  private static class Entry {
    private final Set<Seed> seeds = new LinkedHashSet<>();
    private @Nullable SparseAliasingCFG cfg;
  }

  private final Map<Key, Entry> cache = new HashMap<>();
  private final ListMultimap<Method, Entry> entriesPerMethod = ArrayListMultimap.create();
  private final List<SparseCFGQueryLog> queryLogs = new ArrayList<>();
  private final PropagationCounter counter = new PropagationCounter();

  /**
   * Builds a new sparse graph.
   *
   * @param initialQueryVal the variable of the backward query (without unbalanced marker)
   * @param initialQueryStmt the statement of the backward query
   * @param method the method to build the graph for
   * @param seeds the values (without unbalanced marker) and statements to build the graph for
   * @param requiredStmts statements that have to remain in the graph
   * @param queryLog log to record statistics of the build
   * @return the sparse graph or null if it cannot be built for the seeds
   */
  protected abstract @Nullable SparseAliasingCFG build(
      Val initialQueryVal,
      Statement initialQueryStmt,
      Method method,
      Collection<Seed> seeds,
      Collection<Statement> requiredStmts,
      SparseCFGQueryLog queryLog);

  /**
   * A graph is only used for the values it tracks, because it may not contain the statements that
   * are relevant for other values.
   */
  private static boolean isApplicable(SparseAliasingCFG cfg, Statement stmt, @Nullable Val val) {
    return cfg.contains(stmt) && val != null && cfg.tracks(val.asBalanced());
  }

  @Override
  public synchronized @Nullable SparseAliasingCFG getSparseCFGForForwardPropagation(
      Method method, Statement stmt, @Nullable Val val) {
    for (Entry entry : entriesPerMethod.get(method)) {
      if (entry.cfg != null && isApplicable(entry.cfg, stmt, val)) {
        return entry.cfg;
      }
    }
    return null;
  }

  @Override
  public synchronized @Nullable SparseAliasingCFG getSparseCFGForBackwardPropagation(
      Val initialQueryVal,
      Statement initialQueryStmt,
      Method currentMethod,
      Val currentVal,
      Statement currentStmt) {
    Val initialVal = initialQueryVal.asBalanced();
    Key key = new Key(currentMethod, initialVal, initialQueryStmt);
    Entry entry = cache.get(key);
    if (entry == null) {
      entry = new Entry();
      cache.put(key, entry);
      entriesPerMethod.put(currentMethod, entry);
    }

    if (entry.cfg != null && isApplicable(entry.cfg, currentStmt, currentVal)) {
      return entry.cfg;
    }

    Seed seed = new Seed(currentVal.asBalanced(), currentStmt);
    if (!entry.seeds.add(seed)) {
      // The graph was already built for this seed: it cannot be used for the statement
      return null;
    }

    SparseCFGQueryLog queryLog = new SparseCFGQueryLog(SparseCFGQueryLog.QueryDirection.BWD);
    queryLog.logStart();
    SparseAliasingCFG cfg =
        build(
            initialVal,
            initialQueryStmt,
            currentMethod,
            entry.seeds,
            getRequiredStmts(currentMethod, initialQueryStmt),
            queryLog);
    queryLog.logEnd();
    queryLogs.add(queryLog);

    if (cfg == null) {
      entry.seeds.remove(seed);
      return null;
    }
    entry.cfg = cfg;
    return isApplicable(cfg, currentStmt, currentVal) ? cfg : null;
  }

  /**
   * The results of a backward query are read at the edge from the query statement to its successor.
   * Thus, the forward propagations have to pass this edge, i.e. the query statement and its
   * successors are kept in all graphs of the query's method.
   */
  private static Collection<Statement> getRequiredStmts(Method method, Statement initialQueryStmt) {
    if (!method.equals(initialQueryStmt.getMethod())) {
      return Collections.emptySet();
    }

    Set<Statement> requiredStmts = new HashSet<>();
    requiredStmts.add(initialQueryStmt);
    requiredStmts.addAll(method.getControlFlowGraph().getSuccsOf(initialQueryStmt));
    return requiredStmts;
  }

  @Override
  public synchronized List<SparseCFGQueryLog> getQueryLogs() {
    return Collections.unmodifiableList(queryLogs);
  }

  @Override
  public PropagationCounter getPropagationCounter() {
    return counter;
  }
}
