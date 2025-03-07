package sparse;

import boomerang.scope.Method;
import boomerang.scope.Statement;
import sparse.eval.PropagationCounter;

public interface SparsificationStrategy<M extends Method, S extends Statement> {

  SparseCFGCache<M, S> getInstance(boolean ignoreAfterQuery);

  PropagationCounter getCounter();

  SparsificationStrategy<Method, Statement> NONE = new NoSparsificationStrategy();

  class NoSparsificationStrategy implements SparsificationStrategy<Method, Statement> {
    @Override
    public SparseCFGCache<Method, Statement> getInstance(boolean ignoreAfterQuery) {
      // TODO [ms]: not used in code (when commented code is re-enabled)
      throw new UnsupportedOperationException("not implemented yet.");
    }

    @Override
    public PropagationCounter getCounter() {
      return new PropagationCounter();
    }
  }
}
