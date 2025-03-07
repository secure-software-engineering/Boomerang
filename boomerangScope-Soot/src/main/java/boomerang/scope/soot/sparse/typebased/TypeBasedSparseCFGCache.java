package boomerang.scope.soot.sparse.typebased;

import boomerang.scope.Method;
import boomerang.scope.Statement;
import boomerang.scope.Val;
import boomerang.scope.soot.jimple.JimpleMethod;
import boomerang.scope.soot.sparse.SootAdapter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import soot.SootMethod;
import soot.jimple.Stmt;
import sparse.SparseAliasingCFG;
import sparse.SparseCFGCache;
import sparse.eval.SparseCFGQueryLog;

public class TypeBasedSparseCFGCache implements SparseCFGCache<Method, Statement> {

  List<SparseCFGQueryLog> logList = new ArrayList<>();

  Map<String, SparseAliasingCFG> cache;
  TypeBasedSparseCFGBuilder sparseCFGBuilder;

  private static TypeBasedSparseCFGCache INSTANCE;

  private TypeBasedSparseCFGCache() {}

  public static TypeBasedSparseCFGCache getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new TypeBasedSparseCFGCache(new TypeBasedSparseCFGBuilder(true));
    }
    return INSTANCE;
  }

  private TypeBasedSparseCFGCache(TypeBasedSparseCFGBuilder sparseCFGBuilder) {
    this.cache = new HashMap<>();
    this.sparseCFGBuilder = sparseCFGBuilder;
  }

  @Override
  public SparseAliasingCFG getSparseCFGForForwardPropagation(Method m, Statement stmt, Val val) {
    for (String s : cache.keySet()) {
      // TODO: [ms] rework casting
      if (s.startsWith(((JimpleMethod) m).getDelegate().getSignature())) {
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
      Statement initialQueryStmt,
      Method currentMethod,
      Val currentVal,
      Statement currentStmt) {

    SootMethod sootSurrentMethod = SootAdapter.asSootMethod(currentMethod);
    Stmt sootInitialQueryStmt = SootAdapter.asStmt(initialQueryStmt);
    Stmt sootCurrentStmt = SootAdapter.asStmt(currentStmt);
    //    Value sootInitialQueryVal = SootAdapter.asValue(initialQueryVal);
    //    Value sootCurrentQueryVal = SootAdapter.asValue(currentVal);

    String key = sootSurrentMethod.getSignature() + "-" + initialQueryVal + "-" + initialQueryStmt;

    // currentStmt must be part of the sparseCFG that was built for the initialQueryStmt
    // if not we'll built another sparseCFG for the currentStmt
    if (cache.containsKey(key)) {
      if (cache.get(key).getGraph().nodes().contains(sootCurrentStmt)) {
        SparseCFGQueryLog queryLog =
            new SparseCFGQueryLog(true, SparseCFGQueryLog.QueryDirection.BWD);
        logList.add(queryLog);
        return cache.get(key);
      } else {
        SparseCFGQueryLog queryLog =
            new SparseCFGQueryLog(false, SparseCFGQueryLog.QueryDirection.BWD);
        queryLog.logStart();
        SparseAliasingCFG cfg =
            sparseCFGBuilder.buildSparseCFG(
                initialQueryVal, sootSurrentMethod, sootCurrentStmt, queryLog);
        queryLog.logEnd();
        cache.put(key + currentStmt, cfg);
        logList.add(queryLog);
        return cfg;
      }
    } else if (cache.containsKey(key + currentStmt)) {
      SparseCFGQueryLog queryLog =
          new SparseCFGQueryLog(true, SparseCFGQueryLog.QueryDirection.BWD);
      logList.add(queryLog);
      return cache.get(key + currentStmt);
    } else {
      SparseCFGQueryLog queryLog =
          new SparseCFGQueryLog(false, SparseCFGQueryLog.QueryDirection.BWD);
      queryLog.logStart();
      SparseAliasingCFG cfg =
          sparseCFGBuilder.buildSparseCFG(
              initialQueryVal, sootSurrentMethod, sootCurrentStmt, queryLog);
      queryLog.logEnd();
      cache.put(key, cfg);
      logList.add(queryLog);
      return cfg;
    }
  }

  @Override
  public List<SparseCFGQueryLog> getQueryLogs() {
    return logList;
  }
}
