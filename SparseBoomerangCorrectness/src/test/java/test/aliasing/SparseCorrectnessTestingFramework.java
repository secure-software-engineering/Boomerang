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
package test.aliasing;

import boomerang.BackwardQuery;
import boomerang.Boomerang;
import boomerang.options.BoomerangOptions;
import boomerang.scope.ControlFlowGraph;
import boomerang.scope.FrameworkScope;
import boomerang.scope.IInstanceFieldRef;
import boomerang.scope.Method;
import boomerang.scope.Statement;
import boomerang.solver.Strategies;
import boomerang.sparse.SparsificationStrategy;
import boomerang.util.AccessPath;
import boomerang.utils.MethodWrapper;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import test.TestingFramework;

/**
 * Runs backward queries on the PointerBench targets with different {@link SparsificationStrategy
 * sparsification strategies}. The framework is selected via the system property {@code testSetup}.
 */
public class SparseCorrectnessTestingFramework extends TestingFramework {

  private static final String MAIN_METHOD = "main";
  private static final String QUERY_METHOD = "query";

  private FrameworkScope frameworkScope;

  /**
   * Loads the target class with its main method as entry point.
   *
   * @param targetClass the fully qualified name of the target class
   */
  public void initialize(String targetClass) {
    MethodWrapper mainMethod =
        new MethodWrapper(
            targetClass, MAIN_METHOD, MethodWrapper.VOID, List.of("java.lang.String[]"));
    frameworkScope = getFrameworkScope(mainMethod);
  }

  /**
   * Finds the method that contains the query.
   *
   * @param name the name of a method in the target class or null for the main method
   */
  public Method findMethod(String name) {
    Method mainMethod = getTestMethod();
    if (name == null) {
      return mainMethod;
    }

    return mainMethod.getDeclaringClass().getMethods().stream()
        .filter(m -> m.getName().equals(name) && m.isDefined())
        .findFirst()
        .orElseThrow(
            () ->
                new IllegalArgumentException(
                    "Method " + name + " does not exist in " + mainMethod.getDeclaringClass()));
  }

  /**
   * Creates a backward query for the aliases of the query variable. For a local "x_q1", the query
   * asks for the aliases of the argument of the call {@code Benchmark.query(x_q1)}, which is robust
   * against copy propagation in the frameworks. For a field access "x.f", the query asks for the
   * aliases of x after the first store to its field f.
   *
   * @param method the method that contains the query
   * @param queryLHS the name of the query local or "base.field"
   */
  public BackwardQuery createQuery(Method method, String queryLHS) {
    String[] split = queryLHS.split("\\.");
    if (split.length > 1) {
      Statement stmt =
          findStatement(
              method,
              queryLHS,
              s -> s.isFieldStore() && isFieldStore(s.getFieldStore(), split[0], split[1]));
      Collection<Statement> succs = method.getControlFlowGraph().getSuccsOf(stmt);
      if (succs.isEmpty()) {
        throw new IllegalStateException("No successors for " + stmt);
      }
      return BackwardQuery.make(
          new ControlFlowGraph.Edge(stmt, succs.iterator().next()), stmt.getFieldStore().getBase());
    }

    Statement callSite =
        findStatement(
            method,
            queryLHS,
            s ->
                s.containsInvokeExpr()
                    && s.getInvokeExpr().getDeclaredMethod().getName().equals(QUERY_METHOD));
    Collection<Statement> preds = method.getControlFlowGraph().getPredsOf(callSite);
    if (preds.isEmpty()) {
      throw new IllegalStateException("No predecessors for " + callSite);
    }
    return BackwardQuery.make(
        new ControlFlowGraph.Edge(preds.iterator().next(), callSite),
        callSite.getInvokeExpr().getArg(0));
  }

  private static Statement findStatement(
      Method method, String queryLHS, Predicate<Statement> predicate) {
    return method.getStatements().stream()
        .filter(predicate)
        .findFirst()
        .orElseThrow(
            () ->
                new IllegalArgumentException(
                    "Query for "
                        + queryLHS
                        + " does not exist in "
                        + method
                        + ". Statements in the IR:\n"
                        + method.getStatements().stream()
                            .map(Object::toString)
                            .collect(Collectors.joining("\n"))));
  }

  private static boolean isFieldStore(IInstanceFieldRef fieldRef, String base, String field) {
    return fieldRef.getBase().getVariableName().equals(base)
        && fieldRef.getField().getName().equals(field);
  }

  /**
   * Solves the query with a fresh Boomerang instance.
   *
   * @return the aliases of the query variable
   */
  public Set<AccessPath> getAliases(
      BackwardQuery query, SparsificationStrategy strategy, boolean ignoreAfterQuery) {
    BoomerangOptions options =
        BoomerangOptions.builder()
            .withSparsificationStrategy(strategy)
            .enableIgnoreSparsificationAfterQuery(ignoreAfterQuery)
            .withStaticFieldStrategy(Strategies.StaticFieldStrategy.FLOW_SENSITIVE)
            .enableAllowMultipleQueries(true)
            .build();

    Boomerang boomerang = new Boomerang(frameworkScope, options);
    return boomerang.solve(query).getAllAliases();
  }
}
