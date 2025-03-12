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
package boomerang.scope.soot.sparse.aliasaware;

import boomerang.scope.Statement;
import boomerang.scope.Val;
import boomerang.scope.soot.jimple.JimpleMethod;
import boomerang.scope.soot.jimple.JimpleStatement;
import boomerang.scope.soot.sparse.SootAdapter;
import java.util.*;
import soot.SootMethod;
import soot.jimple.Stmt;
import sparse.SparseAliasingCFG;
import sparse.SparseCFGCache;
import sparse.eval.SparseCFGQueryLog;

public class AliasAwareSparseCFGCache implements SparseCFGCache<JimpleMethod, JimpleStatement> {

  List<SparseCFGQueryLog> logList = new ArrayList<>();

  Map<CacheKey, SparseAliasingCFG> cache;
  AliasAwareSparseCFGBuilder sparseCFGBuilder;

  private static AliasAwareSparseCFGCache INSTANCE;
  private static boolean ignore;

  private AliasAwareSparseCFGCache() {}

  public static AliasAwareSparseCFGCache getInstance(boolean ignoreAfterQuery) {
    if (INSTANCE == null || ignore != ignoreAfterQuery) {
      ignore = ignoreAfterQuery;
      INSTANCE =
          new AliasAwareSparseCFGCache(new AliasAwareSparseCFGBuilder(true, ignoreAfterQuery));
    }
    return INSTANCE;
  }

  private AliasAwareSparseCFGCache(AliasAwareSparseCFGBuilder sparseCFGBuilder) {
    this.cache = new HashMap<>();
    this.sparseCFGBuilder = sparseCFGBuilder;
  }

  // TODO: unify in super
  @Override
  public SparseAliasingCFG getSparseCFGForForwardPropagation(
      JimpleMethod m, JimpleStatement stmt, Val val) {
    // TODO: [ms] check that loop - seems expensive
    for (CacheKey s : cache.keySet()) {
      // TODO: [ms] check refactoring! possibly: getName() is different than getName()
      if (s.getSignature().equals(m.getName())) {
        SparseAliasingCFG sparseAliasingCFG = cache.get(s);
        if (sparseAliasingCFG.getGraph().nodes().contains(stmt)) {
          SparseCFGQueryLog queryLog =
              new SparseCFGQueryLog(true, SparseCFGQueryLog.QueryDirection.FWD);
          logList.add(queryLog);
          return sparseAliasingCFG;
        }
      }
    }
    SparseCFGQueryLog queryLog = new SparseCFGQueryLog(false, SparseCFGQueryLog.QueryDirection.FWD);
    logList.add(queryLog);
    // throw new RuntimeException("CFG not found for:" + m + " s:" + stmt);
    return null;
  }

  @Override
  public synchronized SparseAliasingCFG getSparseCFGForBackwardPropagation(
      Val initialQueryVal,
      JimpleStatement initialQueryStmt,
      JimpleMethod currentMethod,
      Val currentVal,
      JimpleStatement currentStmt) {

    // Value sootInitialQueryVal = SootAdapter.asValue(initialQueryVal);
    // Value sootCurrentQueryVal = SootAdapter.asValue(currentVal);

    // TODO: [ms] necessary?
    SootMethod sootCurrentMethod = SootAdapter.asSootMethod(currentMethod);
    Stmt sootInitialQueryStmt = SootAdapter.asStmt(initialQueryStmt);
    Stmt sootCurrentStmt = SootAdapter.asStmt(currentStmt);

    CacheKey key = new CacheKeyImpl(currentMethod, initialQueryVal, initialQueryStmt);
    SparseAliasingCFG sparseAliasingCFG = cache.get(key);
    if (sparseAliasingCFG != null) {
      if (sparseAliasingCFG.getGraph().nodes().contains(sootCurrentStmt)) {
        SparseCFGQueryLog queryLog =
            new SparseCFGQueryLog(true, SparseCFGQueryLog.QueryDirection.BWD);
        logList.add(queryLog);
        return sparseAliasingCFG;
      } else {
        SparseCFGQueryLog queryLog =
            new SparseCFGQueryLog(false, SparseCFGQueryLog.QueryDirection.BWD);
        queryLog.logStart();
        SparseAliasingCFG cfg =
            sparseCFGBuilder.buildSparseCFG(
                initialQueryVal, sootCurrentMethod, currentVal, sootCurrentStmt, queryLog);
        queryLog.logEnd();
        cache.put(new ChainedCacheKeyImpl(key, currentStmt), cfg);
        logList.add(queryLog);
        return cfg;
      }
    }

    key = new ChainedCacheKeyImpl(key, currentStmt);
    sparseAliasingCFG = cache.get(key);
    if (sparseAliasingCFG != null) {
      SparseCFGQueryLog queryLog =
          new SparseCFGQueryLog(true, SparseCFGQueryLog.QueryDirection.BWD);
      logList.add(queryLog);
      return sparseAliasingCFG;
    }

    SparseCFGQueryLog queryLog = new SparseCFGQueryLog(false, SparseCFGQueryLog.QueryDirection.BWD);
    queryLog.logStart();
    SparseAliasingCFG cfg =
        sparseCFGBuilder.buildSparseCFG(
            initialQueryVal, sootCurrentMethod, currentVal, sootCurrentStmt, queryLog);
    queryLog.logEnd();
    cache.put(key, cfg);
    logList.add(queryLog);
    return cfg;
  }

  @Override
  public List<SparseCFGQueryLog> getQueryLogs() {
    return logList;
  }

  private interface CacheKey {
    Statement getQueryStmt();

    JimpleMethod getSignature();

    Val getInitialQueryVal();
  }

  private static class ChainedCacheKeyImpl implements CacheKey {
    private final CacheKey previousKey;
    private final Statement queryStmt;

    private ChainedCacheKeyImpl(CacheKey previousKey, Statement queryStmt) {
      this.previousKey = previousKey;
      this.queryStmt = queryStmt;
    }

    public Statement getQueryStmt() {
      return queryStmt;
    }

    public JimpleMethod getSignature() {
      return previousKey.getSignature();
    }

    public Val getInitialQueryVal() {
      return previousKey.getInitialQueryVal();
    }

    @Override
    public boolean equals(Object obj) {
      if (!(obj instanceof CacheKey)) {
        return false;
      }
      CacheKey obj1 = (CacheKey) obj;
      return getSignature().equals(obj1.getSignature())
          && getQueryStmt().equals(obj1.getQueryStmt())
          && getInitialQueryVal().equals(obj1.getInitialQueryVal());
    }

    @Override
    public int hashCode() {
      return previousKey.getSignature().hashCode();
    }
  }

  private static class CacheKeyImpl implements CacheKey {
    private final JimpleMethod signature;
    private final Val initialQueryVal;
    private final Statement queryStmt;

    public CacheKeyImpl(
        JimpleMethod signature, Val initialQueryVal, Statement sootInitialQueryStmt) {
      this.signature = signature;
      this.initialQueryVal = initialQueryVal;
      this.queryStmt = sootInitialQueryStmt;
    }

    public JimpleMethod getSignature() {
      return signature;
    }

    public Val getInitialQueryVal() {
      return initialQueryVal;
    }

    public Statement getQueryStmt() {
      return queryStmt;
    }

    @Override
    public boolean equals(Object obj) {
      if (!(obj instanceof CacheKey)) {
        return false;
      }
      CacheKey other = (CacheKey) obj;
      return getSignature().equals(other.getSignature())
          && getQueryStmt().equals(other.getQueryStmt())
          && getInitialQueryVal().equals(other.getInitialQueryVal());
    }

    @Override
    public int hashCode() {
      return signature.hashCode();
    }
  }
}
