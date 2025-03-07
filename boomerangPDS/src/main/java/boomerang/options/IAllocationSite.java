package boomerang.options;

import boomerang.scope.AllocVal;
import boomerang.scope.Method;
import boomerang.scope.Statement;
import boomerang.scope.Val;
import java.util.Optional;

/**
 * Interface that defines the computation of allocation sites when executing Boomerang queries.
 * Classes that implement this interface can be set in the {@link BoomerangOptions} to define custom
 * allocation sites.
 */
public interface IAllocationSite {

  /**
   * Compute an allocation site for given statement. For each traversed statement in the program,
   * Boomerang invokes this method to check whether it should generate an allocation site at this
   * statement. If the returned {@link Optional} is not empty, it creates a corresponding {@link
   * boomerang.ForwardQuery} with the defined {@link AllocVal} and adds it to the resulting
   * allocation sites. Additionally, Boomerang passes the target variable and each collected alias
   * as a fact to this method. This allows to filter statements that may not be relevant for the
   * value of interest. A possible implementation could look like this:
   *
   * <pre>{@code
   * public Optional<AllocVal> getAllocationSite(Method method, Statement statement, Val fact) {
   *     // Make sure that we have an assignment
   *     if (!statement.isAssign()) {
   *         return Optional.empty();
   *     }
   *
   *     // Do not consider statements where the target variable (leftOp) does not alias with our
   *     // value of interest
   *     if (!statement.getLeftOp().equals(fact)) {
   *         return Optional.empty();
   *     }
   *
   *     // Here, we define only constant values as allowed allocation sites
   *     if (statement.getRightOp().isConstant()) {
   *         AllocVal allocVal = new AllocVal(statement.getLeftOp(), statement, statement.getRightOp());
   *         return Optional.of(allocVal);
   *     }
   *
   *     return Optional.empty();
   * }
   *
   * }</pre>
   *
   * When using this implementation, Boomerang returns all allocation sites where the statement
   * assign a constant to the value of interest. The class {@link DefaultAllocationSite} has a more
   * sophisticated implementation.
   *
   * @param method the method from the statement
   * @param statement the statement where an allocation site may be generated
   * @param fact the value of interest or a collected alias
   * @return an existing allocation site if the statement and fact should generate it
   */
  Optional<AllocVal> getAllocationSite(Method method, Statement statement, Val fact);
}
