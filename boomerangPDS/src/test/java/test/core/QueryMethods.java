package test.core;

import java.math.BigInteger;

/**
 * Class for methods that act as query statements. The methods parameter describes the variable that
 * a query is issued for.
 */
public class QueryMethods {

  public static void queryFor(@SuppressWarnings("unused") Object variable) {}

  public static void queryFor1(
      @SuppressWarnings("unused") Object variable,
      @SuppressWarnings("unused") Class<?> interfaceType) {}

  public static void queryFor2(
      @SuppressWarnings("unused") Object variable,
      @SuppressWarnings("unused") Class<?> interfaceType) {}

  public static void accessPathQueryFor(
      @SuppressWarnings("unused") Object variable, @SuppressWarnings("unused") String aliases) {}

  public static void queryForAndNotEmpty(@SuppressWarnings("unused") Object variable) {}

  public static void intQueryFor(
      @SuppressWarnings("unused") int variable, @SuppressWarnings("unused") String value) {}

  public static void intQueryFor(
      @SuppressWarnings("unused") BigInteger variable, @SuppressWarnings("unused") String value) {}

  /**
   * A call to this method flags the object as at the call statement as not reachable by the
   * analysis.
   *
   * @param variable the object that should not be reachable
   */
  public static void unreachable(@SuppressWarnings("unused") Object variable) {}
}
