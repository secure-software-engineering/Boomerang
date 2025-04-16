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
package boomerang.guided;

import boomerang.BackwardQuery;
import boomerang.Boomerang;
import boomerang.ForwardQuery;
import boomerang.flowfunction.DefaultBackwardFlowFunctionOptions;
import boomerang.flowfunction.DefaultForwardFlowFunctionOptions;
import boomerang.guided.flowfunction.CustomBackwardFlowFunction;
import boomerang.guided.flowfunction.CustomForwardFlowFunction;
import boomerang.guided.targets.CustomFlowFunctionIntTarget;
import boomerang.guided.targets.CustomFlowFunctionTarget;
import boomerang.options.BoomerangOptions;
import boomerang.options.IntAndStringAllocationSite;
import boomerang.results.BackwardBoomerangResults;
import boomerang.results.ForwardBoomerangResults;
import boomerang.scope.AllocVal;
import boomerang.scope.ControlFlowGraph.Edge;
import boomerang.scope.FrameworkScope;
import boomerang.scope.Method;
import boomerang.scope.Statement;
import boomerang.scope.Val;
import boomerang.solver.BackwardBoomerangSolver;
import boomerang.utils.MethodWrapper;
import com.google.common.collect.Table;
import java.util.List;
import java.util.Optional;
import org.junit.Assert;
import org.junit.Test;
import test.TestingFramework;
import wpds.impl.Weight.NoWeight;

public class CustomFlowFunctionTest {

  @Test
  public void killOnSystemExitBackwardTestInteger() {
    TestingFramework testingFramework = new TestingFramework();

    MethodWrapper methodWrapper =
        new MethodWrapper(
            CustomFlowFunctionIntTarget.class.getName(),
            "main",
            MethodWrapper.VOID,
            List.of("java.lang.String[]"));
    FrameworkScope frameworkScope = testingFramework.getFrameworkScope(methodWrapper);

    Method m = testingFramework.getTestMethod();
    BackwardQuery query = selectQueryForStatement(m);

    Boomerang solver = new Boomerang(frameworkScope, customOptions());

    System.out.println("Solving query: " + query);
    BackwardBoomerangResults<NoWeight> backwardQueryResults = solver.solve(query);
    for (BackwardBoomerangSolver bw : solver.getBackwardSolvers().values()) {
      Assert.assertTrue(bw.getCallAutomaton().getTransitions().size() < 3);
    }
    System.out.println(backwardQueryResults.getAllocationSites());

    // For the query no allocation site is found, as between queryFor and the allocation site there
    // exists a System.exit call.
    Assert.assertTrue(backwardQueryResults.isEmpty());
  }

  /*
  soot uses 1829 classes here:
  methodname: <init>
  methodname: exit
  methodname: queryFor
  Solving query: BackwardQuery: (z (boomerang.guided.targets.CustomFlowFunctionTarget.<boomerang.guided.targets.CustomFlowFunctionTarget: void main(java.lang.String[])>),exit(y) -> queryFor(z))
  {}
  * */
  @Test
  public void killOnSystemExitBackwardTest() {
    TestingFramework testingFramework = new TestingFramework();

    MethodWrapper methodWrapper =
        new MethodWrapper(
            CustomFlowFunctionTarget.class.getName(),
            "main",
            MethodWrapper.VOID,
            List.of("java.lang.String[]"));
    FrameworkScope frameworkScope = testingFramework.getFrameworkScope(methodWrapper);

    Method m = testingFramework.getTestMethod();
    BackwardQuery query = selectQueryForStatement(m);

    Boomerang solver = new Boomerang(frameworkScope, customOptions());

    System.out.println("Solving query: " + query);
    BackwardBoomerangResults<NoWeight> backwardQueryResults = solver.solve(query);
    System.out.println(backwardQueryResults.getAllocationSites());

    // For the query no allocation site is found, as between queryFor and the allocation site there
    // exists a System.exit call.
    Assert.assertTrue(backwardQueryResults.isEmpty());
  }

  @Test
  public void killOnSystemExitForwardTest() {
    TestingFramework testingFramework = new TestingFramework();

    MethodWrapper methodWrapper =
        new MethodWrapper(
            CustomFlowFunctionTarget.class.getName(),
            "main",
            MethodWrapper.VOID,
            List.of("java.lang.String[]"));
    FrameworkScope frameworkScope = testingFramework.getFrameworkScope(methodWrapper);

    Method m = testingFramework.getTestMethod();
    ForwardQuery query = selectFirstIntAssignment(m);

    Boomerang solver = new Boomerang(frameworkScope, customOptions());

    System.out.println("Solving query: " + query);
    ForwardBoomerangResults<NoWeight> res = solver.solve(query);
    System.out.println(res.asEdgeValWeightTable());

    boolean t =
        res.asStatementValWeightTable().cellSet().stream()
            .map(Table.Cell::getRowKey)
            .anyMatch(
                statement ->
                    statement.containsInvokeExpr()
                        && statement
                            .getInvokeExpr()
                            .getDeclaredMethod()
                            .getName()
                            .equals("queryFor"));
    Assert.assertFalse(t);
  }

  public static BackwardQuery selectQueryForStatement(Method method) {
    Optional<Statement> queryStatement =
        method.getStatements().stream()
            .filter(Statement::containsInvokeExpr)
            .filter(
                x -> {
                  System.out.println(
                      "methodname: " + x.getInvokeExpr().getDeclaredMethod().getName());
                  return x.getInvokeExpr().getDeclaredMethod().getName().equals("queryFor");
                })
            .findFirst();
    if (queryStatement.isEmpty()) {
      Assert.fail("No query statement found in method " + method.getName());
    }
    Val arg = queryStatement.get().getInvokeExpr().getArg(0);

    Optional<Statement> predecessor =
        method.getControlFlowGraph().getPredsOf(queryStatement.get()).stream().findFirst();
    if (predecessor.isEmpty()) {
      Assert.fail("No predecessor found for " + queryStatement);
    }

    Edge cfgEdge = new Edge(predecessor.get(), queryStatement.get());
    return BackwardQuery.make(cfgEdge, arg);
  }

  public static ForwardQuery selectFirstIntAssignment(Method method) {
    method.getStatements().forEach(x -> System.out.println(x.toString()));
    Optional<Statement> intAssignStmt =
        method.getStatements().stream()
            .filter(x -> x.isAssignStmt() && !x.getLeftOp().getType().isRefType())
            .findFirst();
    if (intAssignStmt.isEmpty()) {
      Assert.fail("No assignment found in method " + method.getName());
    }

    AllocVal arg =
        new AllocVal(
            intAssignStmt.get().getLeftOp(), intAssignStmt.get(), intAssignStmt.get().getRightOp());

    Optional<Statement> succs =
        method.getControlFlowGraph().getSuccsOf(intAssignStmt.get()).stream().findFirst();
    if (succs.isEmpty()) {
      Assert.fail("No successor found for " + intAssignStmt);
    }

    Edge cfgEdge = new Edge(intAssignStmt.get(), succs.get());
    return new ForwardQuery(cfgEdge, arg);
  }

  private static BoomerangOptions customOptions() {
    return BoomerangOptions.builder()
        .withAllocationSite(new IntAndStringAllocationSite())
        .withForwardFlowFunction(
            new CustomForwardFlowFunction(DefaultForwardFlowFunctionOptions.DEFAULT()))
        .withBackwardFlowFunction(
            new CustomBackwardFlowFunction(DefaultBackwardFlowFunctionOptions.DEFAULT()))
        .build();
  }
}
