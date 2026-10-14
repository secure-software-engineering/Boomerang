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
package boomerang.callgraph;

import boomerang.guided.targets.PingPongInterproceduralTarget;
import boomerang.scope.CallGraph;
import boomerang.scope.CallGraph.Edge;
import boomerang.scope.FrameworkScope;
import boomerang.scope.LazyCallGraph;
import boomerang.scope.Method;
import boomerang.scope.Statement;
import boomerang.utils.MethodWrapper;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import test.TestingFramework;

/** Checks that the lazily forwarding call graphs of all scopes answer consistently. */
public class LazyCallGraphConsistencyTest {

  @Test
  public void edgesOutOfAndIntoAgree() {
    TestingFramework testingFramework = new TestingFramework();
    MethodWrapper methodWrapper =
        new MethodWrapper(
            PingPongInterproceduralTarget.class.getName(),
            "main",
            MethodWrapper.VOID,
            List.of("java.lang.String[]"));
    FrameworkScope frameworkScope = testingFramework.getFrameworkScope(methodWrapper);
    CallGraph callGraph = frameworkScope.getCallGraph();

    Assertions.assertInstanceOf(LazyCallGraph.class, callGraph);
    Assertions.assertTrue(
        callGraph.getEntryPoints().contains(testingFramework.getTestMethod()),
        "Test method is no entry point");

    Set<Edge> outEdges = new HashSet<>();
    for (Method m : callGraph.getReachableMethods()) {
      if (!m.isDefined()) {
        continue;
      }

      for (Statement s : m.getStatements()) {
        if (!s.containsInvokeExpr()) {
          continue;
        }

        Collection<Edge> edges = callGraph.edgesOutOf(s);
        Assertions.assertSame(edges, callGraph.edgesOutOf(s), "edgesOutOf is not memoized");
        for (Edge e : edges) {
          Assertions.assertEquals(s, e.src());
          Assertions.assertTrue(
              callGraph.getReachableMethods().contains(e.tgt()), "Target not reachable: " + e);
          Assertions.assertTrue(
              callGraph.edgesInto(e.tgt()).contains(e), "Missing in edgesInto: " + e);
          outEdges.add(e);
        }
      }
    }

    Assertions.assertFalse(outEdges.isEmpty(), "No call edges found");
    Assertions.assertTrue(callGraph.getEdges().containsAll(outEdges));
    Assertions.assertEquals(callGraph.getEdges().size(), callGraph.size());
    for (Edge e : callGraph.getEdges()) {
      Assertions.assertTrue(callGraph.edgesOutOf(e.src()).contains(e), "Not in edgesOutOf: " + e);
    }

    for (Method m : callGraph.getReachableMethods()) {
      for (Edge e : callGraph.edgesInto(m)) {
        Assertions.assertEquals(m, e.tgt());
        Assertions.assertTrue(
            e.src().getMethod().getStatements().contains(e.src()),
            "Call site is not a statement of its method: " + e);
        Assertions.assertTrue(
            callGraph.edgesOutOf(e.src()).contains(e), "Missing in edgesOutOf: " + e);
      }
    }

    for (Statement s : callGraph.getFieldStoreStatements().values()) {
      Assertions.assertTrue(s.isStaticFieldStore());
      Assertions.assertTrue(callGraph.getReachableMethods().contains(s.getMethod()));
    }
    for (Statement s : callGraph.getFieldLoadStatements().values()) {
      Assertions.assertTrue(s.isStaticFieldLoad());
      Assertions.assertTrue(callGraph.getReachableMethods().contains(s.getMethod()));
    }
  }
}
