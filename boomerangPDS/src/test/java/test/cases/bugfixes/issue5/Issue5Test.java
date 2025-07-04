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
package test.cases.bugfixes.issue5;

import boomerang.Boomerang;
import boomerang.ForwardQuery;
import boomerang.options.BoomerangOptions;
import boomerang.options.IntAndStringAllocationSite;
import boomerang.results.ForwardBoomerangResults;
import boomerang.scope.AllocVal;
import boomerang.scope.ControlFlowGraph.Edge;
import boomerang.scope.DeclaredMethod;
import boomerang.scope.FrameworkScope;
import boomerang.scope.Method;
import boomerang.scope.Statement;
import boomerang.scope.Type;
import boomerang.scope.Val;
import boomerang.utils.MethodWrapper;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import test.TestingFramework;
import wpds.impl.NoWeight;

/**
 * This code was added to test <a href="https://github.com/CodeShield-Security/SPDS/issues/5">Issue
 * 5</a>. Thanks to @copumpkin for sharing code for testing purpose.
 */
public class Issue5Test {

  private final String target = test.cases.bugfixes.issue5.Test.class.getName();

  @Test
  public void excludeFoo() {
    TestingFramework testingFramework =
        new TestingFramework(Collections.emptyList(), List.of(Foo.class.getName()));

    MethodWrapper methodWrapper = new MethodWrapper(target, "foos", "java.util.List");
    FrameworkScope frameworkScope = testingFramework.getFrameworkScope(methodWrapper);

    assertResults(
        frameworkScope,
        testingFramework.getTestMethod(),
        new MethodWrapper(Foo.class.getName(), "baz"),
        new MethodWrapper(Foo.class.getName(), "bar"),
        new MethodWrapper(Foo.class.getName(), "<init>"));
  }

  @Test
  public void includeFoo() {
    MethodWrapper methodWrapper = new MethodWrapper(target, "foos", "java.util.List");
    TestingFramework testingFramework = new TestingFramework();
    FrameworkScope frameworkScope = testingFramework.getFrameworkScope(methodWrapper);

    assertResults(
        frameworkScope,
        testingFramework.getTestMethod(),
        new MethodWrapper(Foo.class.getName(), "baz"),
        new MethodWrapper(Foo.class.getName(), "bar"),
        new MethodWrapper(Foo.class.getName(), "<init>"),
        new MethodWrapper(java.lang.Object.class.getName(), "<init>"));
  }

  private void assertResults(
      FrameworkScope frameworkScope,
      Method testMethod,
      MethodWrapper... expectedCalledMethodsOnFoo) {
    System.out.println("All method units:");
    for (Statement s : testMethod.getControlFlowGraph().getStatements()) {
      System.out.println("\t" + s.toString());
    }

    Optional<Statement> newFoo =
        testMethod.getControlFlowGraph().getStatements().stream()
            .filter(this::isFooAssignment)
            .findFirst();

    if (newFoo.isEmpty()) {
      Assertions.fail("Could not find instantiation of Foo");
    }

    // This will only show results if set_exclude above gets uncommented
    System.out.println("\nFoo invoked methods:");
    Collection<Statement> statements =
        getMethodsInvokedFromInstanceInStatement(frameworkScope, newFoo.get());

    Collection<MethodWrapper> methodCalledOnFoo = new HashSet<>();
    for (Statement s : statements) {
      System.out.println("\t" + s);
      DeclaredMethod calledMethod = s.getInvokeExpr().getDeclaredMethod();
      System.out.println("\t\t" + calledMethod);

      methodCalledOnFoo.add(calledMethod.toMethodWrapper());
    }

    Assertions.assertEquals(Set.of(expectedCalledMethodsOnFoo), methodCalledOnFoo);
  }

  private static Collection<Statement> getMethodsInvokedFromInstanceInStatement(
      FrameworkScope scopeFactory, Statement queryStatement) {
    AllocVal var =
        new AllocVal(queryStatement.getLeftOp(), queryStatement, queryStatement.getRightOp());
    Optional<Statement> successorStmt =
        queryStatement.getMethod().getControlFlowGraph().getSuccsOf(queryStatement).stream()
            .findFirst();
    if (successorStmt.isEmpty()) {
      Assertions.fail("Could not find successor for " + queryStatement);
    }

    ForwardQuery fwq = new ForwardQuery(new Edge(queryStatement, successorStmt.get()), var);
    Boomerang solver =
        new Boomerang(
            scopeFactory, BoomerangOptions.WITH_ALLOCATION_SITE(new IntAndStringAllocationSite()));
    ForwardBoomerangResults<NoWeight> results = solver.solve(fwq);
    return results.getInvokeStatementsOnInstance();
  }

  private boolean isFooAssignment(Statement statement) {
    if (statement.isAssignStmt()) {
      Val rightOp = statement.getRightOp();

      if (rightOp.isNewExpr()) {
        Type newExprType = rightOp.getNewExprType();

        return newExprType.toString().equals(Foo.class.getName());
      }
    }

    return false;
  }
}
