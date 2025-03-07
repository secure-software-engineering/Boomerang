package boomerang;

import boomerang.scope.Statement;

/**
 * A context is stored within the context graph. And must at least have a statement associated with
 * it. This will be used by the {@link IContextRequester} to retrieve more contexts upon need.
 *
 * @author "Johannes Spaeth"
 */
public interface Context {
  Statement getStmt();

  int hashCode();

  boolean equals(Object obj);
}
