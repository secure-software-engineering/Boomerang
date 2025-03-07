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
