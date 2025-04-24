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
package boomerang.guided;

import boomerang.BackwardQuery;
import boomerang.ForwardQuery;
import boomerang.Query;
import boomerang.QueryGraph;
import boomerang.guided.targets.ArrayContainerTarget;
import boomerang.guided.targets.BasicTarget;
import boomerang.guided.targets.BranchingAfterNewStringTest;
import boomerang.guided.targets.BranchingTest;
import boomerang.guided.targets.ContextSensitiveAndLeftUnbalanced2StacksTarget;
import boomerang.guided.targets.ContextSensitiveAndLeftUnbalancedFieldTarget;
import boomerang.guided.targets.ContextSensitiveAndLeftUnbalancedTarget;
import boomerang.guided.targets.ContextSensitiveAndLeftUnbalancedTarget2;
import boomerang.guided.targets.ContextSensitiveAndLeftUnbalancedThisFieldTarget;
import boomerang.guided.targets.ContextSensitiveTarget;
import boomerang.guided.targets.IntegerCastTarget;
import boomerang.guided.targets.LeftUnbalancedTarget;
import boomerang.guided.targets.NestedContextAndBranchingTarget;
import boomerang.guided.targets.NestedContextTarget;
import boomerang.guided.targets.PingPongInterproceduralTarget;
import boomerang.guided.targets.PingPongTarget;
import boomerang.guided.targets.ValueOfTarget;
import boomerang.guided.targets.WrappedInNewStringInnerTarget;
import boomerang.guided.targets.WrappedInNewStringTarget;
import boomerang.guided.targets.WrappedInStringTwiceTest;
import boomerang.options.BoomerangOptions;
import boomerang.options.IAllocationSite;
import boomerang.options.IntAndStringAllocationSite;
import boomerang.scope.AllocVal;
import boomerang.scope.CallGraph;
import boomerang.scope.ControlFlowGraph.Edge;
import boomerang.scope.FrameworkScope;
import boomerang.scope.Method;
import boomerang.scope.Statement;
import boomerang.scope.Val;
import boomerang.utils.MethodWrapper;
import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.Assert;
import org.junit.Test;
import test.TestingFramework;
import wpds.impl.NoWeight;

public class DemandDrivenGuidedAnalysisTest {

  @Test
  public void integerCastTest() {
    TestingFramework testingFramework = new TestingFramework();

    MethodWrapper methodWrapper =
        new MethodWrapper(
            IntegerCastTarget.class.getName(),
            "main",
            MethodWrapper.VOID,
            List.of("java.lang.String[]"));
    FrameworkScope frameworkScope = testingFramework.getFrameworkScope(methodWrapper);

    Method m = testingFramework.getTestMethod();

    BackwardQuery query = selectFirstBaseOfToString(m);
    Specification spec =
        Specification.create("<java.lang.Integer: ON{B}java.lang.Integer valueOf(GO{B}int)>");
    runAnalysis(frameworkScope, spec, query, 1);
  }

  @Test
  public void basicTarget() {
    TestingFramework testingFramework = new TestingFramework();

    MethodWrapper methodWrapper =
        new MethodWrapper(
            BasicTarget.class.getName(), "main", MethodWrapper.VOID, List.of("java.lang.String[]"));
    FrameworkScope frameworkScope = testingFramework.getFrameworkScope(methodWrapper);

    Method m = testingFramework.getTestMethod();
    BackwardQuery query = selectFirstFileInitArgument(m);

    runAnalysis(frameworkScope, query, "bar");
  }

  @Test
  public void leftUnbalancedTargetTest() {
    TestingFramework testingFramework = new TestingFramework();

    MethodWrapper methodWrapper =
        new MethodWrapper(
            LeftUnbalancedTarget.class.getName(),
            "main",
            MethodWrapper.VOID,
            List.of("java.lang.String[]"));
    FrameworkScope frameworkScope = testingFramework.getFrameworkScope(methodWrapper);

    Method m = testingFramework.getTestMethod();
    Method target = getMethodFromName(frameworkScope.getCallGraph(), m, "bar");

    BackwardQuery query = selectFirstFileInitArgument(target);
    runAnalysis(frameworkScope, query, "bar");
  }

  @Test
  public void contextSensitiveTest() {
    TestingFramework testingFramework = new TestingFramework();

    MethodWrapper methodWrapper =
        new MethodWrapper(
            ContextSensitiveTarget.class.getName(),
            "main",
            MethodWrapper.VOID,
            List.of("java.lang.String[]"));
    FrameworkScope frameworkScope = testingFramework.getFrameworkScope(methodWrapper);

    Method m = testingFramework.getTestMethod();
    BackwardQuery query = selectFirstFileInitArgument(m);

    runAnalysis(frameworkScope, query, "bar");
  }

  @Test
  public void nestedContextTest() {
    TestingFramework testingFramework = new TestingFramework();

    MethodWrapper methodWrapper =
        new MethodWrapper(
            NestedContextTarget.class.getName(),
            "main",
            MethodWrapper.VOID,
            List.of("java.lang.String[]"));
    FrameworkScope frameworkScope = testingFramework.getFrameworkScope(methodWrapper);

    Method m = testingFramework.getTestMethod();
    BackwardQuery query = selectFirstFileInitArgument(m);

    runAnalysis(frameworkScope, query, "bar");
  }

  @Test
  public void nestedContextAndBranchingTest() {
    TestingFramework testingFramework = new TestingFramework();

    MethodWrapper methodWrapper =
        new MethodWrapper(
            NestedContextAndBranchingTarget.class.getName(),
            "main",
            MethodWrapper.VOID,
            List.of("java.lang.String[]"));
    FrameworkScope frameworkScope = testingFramework.getFrameworkScope(methodWrapper);

    Method m = testingFramework.getTestMethod();
    BackwardQuery query = selectFirstFileInitArgument(m);

    runAnalysis(frameworkScope, query, "bar", "foo");
  }

  @Test
  public void contextSensitiveAndLeftUnbalanced2StacksTest() {
    TestingFramework testingFramework = new TestingFramework();

    MethodWrapper methodWrapper =
        new MethodWrapper(
            ContextSensitiveAndLeftUnbalanced2StacksTarget.class.getName(), "context");
    FrameworkScope frameworkScope = testingFramework.getFrameworkScope(methodWrapper);

    Method m = testingFramework.getTestMethod();
    BackwardQuery query = selectFirstFileInitArgument(m);

    runAnalysis(frameworkScope, query, "bar");
  }

  @Test
  public void contextSensitiveAndLeftUnbalancedTest() {
    TestingFramework testingFramework = new TestingFramework();

    MethodWrapper methodWrapper =
        new MethodWrapper(
            ContextSensitiveAndLeftUnbalancedTarget.class.getName(),
            "main",
            MethodWrapper.VOID,
            List.of("java.lang.String[]"));
    FrameworkScope frameworkScope = testingFramework.getFrameworkScope(methodWrapper);

    Method m = testingFramework.getTestMethod();
    Method target = getMethodFromName(frameworkScope.getCallGraph(), m, "context");

    BackwardQuery query = selectFirstFileInitArgument(target);
    runAnalysis(frameworkScope, query, "bar");
  }

  @Test
  public void contextSensitiveAndLeftUnbalancedWithFieldTest() {
    TestingFramework testingFramework = new TestingFramework();

    MethodWrapper methodWrapper =
        new MethodWrapper(
            ContextSensitiveAndLeftUnbalancedFieldTarget.class.getName(),
            "main",
            MethodWrapper.VOID,
            List.of("java.lang.String[]"));
    FrameworkScope frameworkScope = testingFramework.getFrameworkScope(methodWrapper);

    Method m = testingFramework.getTestMethod();
    Method target = getMethodFromName(frameworkScope.getCallGraph(), m, "context");

    BackwardQuery query = selectFirstFileInitArgument(target);
    runAnalysis(frameworkScope, query, "bar");
  }

  @Test
  public void contextSensitiveAndLeftUnbalancedWithThisFieldTest() {
    TestingFramework testingFramework = new TestingFramework();

    MethodWrapper methodWrapper =
        new MethodWrapper(
            ContextSensitiveAndLeftUnbalancedThisFieldTarget.class.getName(),
            "main",
            MethodWrapper.VOID,
            List.of("java.lang.String[]"));
    FrameworkScope frameworkScope = testingFramework.getFrameworkScope(methodWrapper);

    Method m = testingFramework.getTestMethod();
    Method target = getMethodFromName(frameworkScope.getCallGraph(), m, "context");

    BackwardQuery query = selectFirstFileInitArgument(target);
    runAnalysis(frameworkScope, query, "bar");
  }

  @Test
  public void contextSensitiveAndLeftUnbalancedWithFieldTest2() {
    TestingFramework testingFramework = new TestingFramework();

    MethodWrapper methodWrapper =
        new MethodWrapper(
            ContextSensitiveAndLeftUnbalancedTarget2.class.getName(),
            "main",
            MethodWrapper.VOID,
            List.of("java.lang.String[]"));
    FrameworkScope frameworkScope = testingFramework.getFrameworkScope(methodWrapper);

    Method m = testingFramework.getTestMethod();
    Method target = getMethodFromName(frameworkScope.getCallGraph(), m, "context");

    BackwardQuery query = selectFirstBaseOfToString(target);
    runAnalysis(frameworkScope, query, "bar");
  }

  @Test
  public void wrappedInNewStringTest() {
    TestingFramework testingFramework = new TestingFramework();

    MethodWrapper methodWrapper =
        new MethodWrapper(
            WrappedInNewStringTarget.class.getName(),
            "main",
            MethodWrapper.VOID,
            List.of("java.lang.String[]"));
    FrameworkScope frameworkScope = testingFramework.getFrameworkScope(methodWrapper);

    Method m = testingFramework.getTestMethod();
    BackwardQuery query = selectFirstFileInitArgument(m);

    runAnalysis(frameworkScope, query, "bar");
  }

  @Test
  public void wrappedInNewStringInnerTest() {
    TestingFramework testingFramework = new TestingFramework();

    MethodWrapper methodWrapper =
        new MethodWrapper(
            WrappedInNewStringInnerTarget.class.getName(),
            "main",
            MethodWrapper.VOID,
            List.of("java.lang.String[]"));
    FrameworkScope frameworkScope = testingFramework.getFrameworkScope(methodWrapper);

    Method m = testingFramework.getTestMethod();
    BackwardQuery query = selectFirstFileInitArgument(m);

    runAnalysis(frameworkScope, query, "bar");
  }

  @Test
  public void wrappedInNewStringTwiceTest() {
    TestingFramework testingFramework = new TestingFramework();

    MethodWrapper methodWrapper =
        new MethodWrapper(
            WrappedInStringTwiceTest.class.getName(),
            "main",
            MethodWrapper.VOID,
            List.of("java.lang.String[]"));
    FrameworkScope frameworkScope = testingFramework.getFrameworkScope(methodWrapper);

    Method m = testingFramework.getTestMethod();
    BackwardQuery query = selectFirstFileInitArgument(m);

    runAnalysis(frameworkScope, query, "bar");
  }

  @Test
  public void branchingTest() {
    TestingFramework testingFramework = new TestingFramework();

    MethodWrapper methodWrapper =
        new MethodWrapper(
            BranchingTest.class.getName(),
            "main",
            MethodWrapper.VOID,
            List.of("java.lang.String[]"));
    FrameworkScope frameworkScope = testingFramework.getFrameworkScope(methodWrapper);

    Method m = testingFramework.getTestMethod();
    BackwardQuery query = selectFirstFileInitArgument(m);

    runAnalysis(frameworkScope, query, "bar", "foo");
  }

  @Test
  public void branchingAfterNewTest() {
    TestingFramework testingFramework = new TestingFramework();

    MethodWrapper methodWrapper =
        new MethodWrapper(
            BranchingAfterNewStringTest.class.getName(),
            "main",
            MethodWrapper.VOID,
            List.of("java.lang.String[]"));
    FrameworkScope frameworkScope = testingFramework.getFrameworkScope(methodWrapper);

    Method m = testingFramework.getTestMethod();
    BackwardQuery query = selectFirstFileInitArgument(m);

    runAnalysis(frameworkScope, query, "bar", "foo");
  }

  @Test
  public void pingPongTest() {
    TestingFramework testingFramework = new TestingFramework();

    MethodWrapper methodWrapper =
        new MethodWrapper(
            PingPongTarget.class.getName(),
            "main",
            MethodWrapper.VOID,
            List.of("java.lang.String[]"));
    FrameworkScope frameworkScope = testingFramework.getFrameworkScope(methodWrapper);

    Method m = testingFramework.getTestMethod();
    BackwardQuery query = selectFirstFileInitArgument(m);

    runAnalysis(frameworkScope, getPingPongSpecification(), query, "hello", "world");
  }

  @Test
  public void pingPongInterproceduralTest() {
    TestingFramework testingFramework = new TestingFramework();

    MethodWrapper methodWrapper =
        new MethodWrapper(
            PingPongInterproceduralTarget.class.getName(),
            "main",
            MethodWrapper.VOID,
            List.of("java.lang.String[]"));
    FrameworkScope frameworkScope = testingFramework.getFrameworkScope(methodWrapper);

    Method m = testingFramework.getTestMethod();
    BackwardQuery query = selectFirstFileInitArgument(m);

    runAnalysis(frameworkScope, getPingPongSpecification(), query, "hello", "world");
  }

  @Test
  public void arrayContainerTest() {
    TestingFramework testingFramework = new TestingFramework();

    MethodWrapper methodWrapper =
        new MethodWrapper(
            ArrayContainerTarget.class.getName(),
            "main",
            MethodWrapper.VOID,
            List.of("java.lang.String[]"));
    FrameworkScope frameworkScope = testingFramework.getFrameworkScope(methodWrapper);

    Method m = testingFramework.getTestMethod();
    BackwardQuery query = selectFirstBaseOfToString(m);

    runAnalysis(frameworkScope, new ArrayContainerCollectionManager(), query, "hello", "world");
  }

  @Test
  public void valueOfTarget() {
    TestingFramework testingFramework = new TestingFramework();

    MethodWrapper methodWrapper =
        new MethodWrapper(
            ValueOfTarget.class.getName(),
            "main",
            MethodWrapper.VOID,
            List.of("java.lang.String[]"));
    FrameworkScope frameworkScope = testingFramework.getFrameworkScope(methodWrapper);

    Method m = testingFramework.getTestMethod();
    Method target = getMethodFromName(frameworkScope.getCallGraph(), m, "foo");

    BackwardQuery query = selectFirstArgOfQueryTarget(target);
    runAnalysis(frameworkScope, query, 1);
  }

  private Method getMethodFromName(CallGraph callGraph, Method method, String methodName) {
    for (Statement statement : method.getStatements()) {
      for (CallGraph.Edge callee : callGraph.edgesOutOf(statement)) {
        if (callee.tgt().getName().equals(methodName)) {
          return callee.tgt();
        }
      }
    }
    throw new RuntimeException("Could not find call to method " + methodName);
  }

  public static BackwardQuery selectFirstArgOfQueryTarget(Method method) {
    Optional<Statement> newFileStatement =
        method.getStatements().stream()
            .filter(Statement::containsInvokeExpr)
            .filter(
                x ->
                    x.getInvokeExpr().getDeclaredMethod().getName().equals("queryFor")
                        && x.getInvokeExpr()
                            .getDeclaredMethod()
                            .getDeclaringClass()
                            .getFullyQualifiedName()
                            .equals("boomerang.guided.targets.Query"))
            .findFirst();
    if (newFileStatement.isEmpty()) {
      Assert.fail("No new file statement found in method " + method.getName());
    }
    Val arg = newFileStatement.get().getInvokeExpr().getArg(0);

    Optional<Statement> predecessor =
        method.getControlFlowGraph().getPredsOf(newFileStatement.get()).stream().findFirst();
    if (predecessor.isEmpty()) {
      Assert.fail("No predecessor found for statement " + newFileStatement);
    }

    Edge cfgEdge = new Edge(predecessor.get(), newFileStatement.get());
    return BackwardQuery.make(cfgEdge, arg);
  }

  private Specification getPingPongSpecification() {
    return Specification.create(
        "<ON{B}java.lang.StringBuilder: java.lang.StringBuilder append(GO{B}java.lang.String)>",
        "<ON{F}java.lang.StringBuilder: java.lang.StringBuilder append(GO{B}java.lang.String)>",
        "<ON{F}java.lang.StringBuilder: GO{F}java.lang.StringBuilder append(java.lang.String)>",
        "<GO{B}java.lang.StringBuilder: ON{B}java.lang.String toString()>");
  }

  public static BackwardQuery selectFirstFileInitArgument(Method method) {
    Optional<Statement> newFileStatement =
        method.getStatements().stream()
            .filter(Statement::containsInvokeExpr)
            .filter(
                x ->
                    x.getInvokeExpr().getDeclaredMethod().getName().equals("<init>")
                        && x.getInvokeExpr()
                            .getDeclaredMethod()
                            .getDeclaringClass()
                            .getFullyQualifiedName()
                            .equals("java.io.File"))
            .findFirst();
    if (newFileStatement.isEmpty()) {
      Assert.fail("No new file statement found in method " + method.getName());
    }

    Val arg = newFileStatement.get().getInvokeExpr().getArg(0);

    Optional<Statement> predecessor =
        method.getControlFlowGraph().getPredsOf(newFileStatement.get()).stream().findFirst();
    if (predecessor.isEmpty()) {
      Assert.fail("No predecessor found for statement " + newFileStatement);
    }

    Edge cfgEdge = new Edge(predecessor.get(), newFileStatement.get());
    return BackwardQuery.make(cfgEdge, arg);
  }

  public static BackwardQuery selectFirstBaseOfToString(Method method) {
    Optional<Statement> toStringCall =
        method.getStatements().stream()
            .filter(Statement::containsInvokeExpr)
            .filter(x -> x.getInvokeExpr().getDeclaredMethod().getName().equals("toString"))
            .findFirst();
    if (toStringCall.isEmpty()) {
      Assert.fail("No call to toString() found in method " + method.getName());
    }

    Val arg = toStringCall.get().getInvokeExpr().getBase();
    Optional<Statement> predecessor =
        method.getControlFlowGraph().getPredsOf(toStringCall.get()).stream().findFirst();
    if (predecessor.isEmpty()) {
      Assert.fail("No predecessor found for statement " + toStringCall);
    }

    Edge cfgEdge = new Edge(predecessor.get(), toStringCall.get());
    return BackwardQuery.make(cfgEdge, arg);
  }

  protected void runAnalysis(
      FrameworkScope scopeFactory, BackwardQuery query, Object... expectedValues) {
    Specification specification =
        Specification.create(
            "<java.lang.Integer: ON{B}java.lang.Integer valueOf(GO{B}int)>",
            "<ON{B}java.lang.Integer: java.lang.Integer <init>(GO{B}int)>",
            "<GO{F}java.lang.String: void <init>(ON{F}java.lang.String)>",
            "<ON{B}java.lang.String: void <init>(GO{B}java.lang.String)>",
            "<GO{B}java.lang.String: ON{B}byte[] getBytes()>");
    runAnalysis(scopeFactory, specification, query, expectedValues);
  }

  private boolean isStringOrIntAllocation(Statement stmt) {
    return stmt.isAssignStmt()
        && (stmt.getRightOp().isIntConstant() || stmt.getRightOp().isStringConstant());
  }

  protected void runAnalysis(
      FrameworkScope scopeFactory,
      Specification specification,
      BackwardQuery query,
      Object... expectedValues) {
    runAnalysis(
        scopeFactory, new SimpleSpecificationGuidedManager(specification), query, expectedValues);
  }

  protected void runAnalysis(
      FrameworkScope scopeFactory,
      IDemandDrivenGuidedManager queryManager,
      BackwardQuery query,
      Object... expectedValues) {
    BoomerangOptions options =
        BoomerangOptions.builder()
            .withAllocationSite(allocationSite())
            .enableAllowMultipleQueries(true)
            .build();

    DemandDrivenGuidedAnalysis demandDrivenGuidedAnalysis =
        new DemandDrivenGuidedAnalysis(queryManager, options, scopeFactory);

    QueryGraph<NoWeight> queryGraph = demandDrivenGuidedAnalysis.run(query);
    demandDrivenGuidedAnalysis.cleanUp();

    //  Assert.assertFalse(queryGraph.getNodes().isEmpty());

    // Filter out query graph's node to only return the queries of interest (ForwardQueries &
    // String/Int Allocation sites).
    Stream<Query> res =
        queryGraph.getNodes().stream()
            .filter(
                x ->
                    x instanceof ForwardQuery
                        && isStringOrIntAllocation(x.asNode().stmt().getStart()));

    Set<? extends Serializable> collect =
        res.map(t -> ((AllocVal) t.var()).getAllocVal())
            .filter(x -> x.isStringConstant() || x.isIntConstant())
            .map(x -> (x.isIntConstant() ? x.getIntValue() : x.getStringValue()))
            .collect(Collectors.toSet());

    Assert.assertEquals(Set.of(expectedValues), collect);
  }

  private IAllocationSite allocationSite() {
    return new IntAndStringAllocationSite() {

      @Override
      public Optional<AllocVal> getAllocationSite(Method m, Statement stmt, Val fact) {
        if (stmt.isAssignStmt() && stmt.getLeftOp().equals(fact) && isStringOrIntAllocation(stmt)) {
          return Optional.of(new AllocVal(stmt.getLeftOp(), stmt, stmt.getRightOp()));
        }
        return super.getAllocationSite(m, stmt, fact);
      }
    };
  }
}
