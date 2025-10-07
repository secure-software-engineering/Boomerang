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
package test.options.flowfunctions;

import boomerang.ForwardQuery;
import boomerang.flowfunction.DefaultBackwardFlowFunction;
import boomerang.flowfunction.DefaultFlowFunctionFactory;
import boomerang.flowfunction.DefaultForwardFlowFunction;
import boomerang.flowfunction.IBackwardFlowFunction;
import boomerang.flowfunction.IForwardFlowFunction;
import boomerang.options.BoomerangOptions;
import boomerang.options.IAllocationSite;
import boomerang.scope.ControlFlowGraph;
import boomerang.scope.FrameworkScope;
import boomerang.scope.InvokeExpr;
import boomerang.scope.Statement;
import boomerang.scope.Val;
import boomerang.solver.BackwardBoomerangSolver;
import boomerang.solver.ForwardBoomerangSolver;
import boomerang.solver.Strategies;
import boomerang.utils.MethodWrapper;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import sync.pds.solver.nodes.Node;
import test.options.OptionAssertions;
import test.options.OptionsTestInterceptor;
import test.options.TestOptions;
import wpds.interfaces.State;

@ExtendWith(OptionsTestInterceptor.class)
public class FlowFunctionFactoryTest {

  private static final MethodWrapper STRING_VALUE_OF =
      new MethodWrapper("java.lang.String", "valueOf", "java.lang.String", List.of("int"));

  @Test
  @TestOptions(
      expectedAllocSites = {"10"},
      flowFunctionFactory = ExtendedFlowFunctionFactory.class)
  public void extendedFlowFunctionFactoryTest() {
    int i = 10;
    String s = String.valueOf(i);
    OptionAssertions.queryFor(s);
  }

  /**
   * Flow functions that continue the dataflow when reaching a statement {@link
   * java.lang.String#valueOf(int)}. The dataflow continues with the parameter value, e.g. for
   *
   * <pre>{@code
   * int i = 10;
   * String s = String.valueOf(i);
   * OptionAssertions.queryFor(s);
   * }</pre>
   *
   * Boomerang would find the allocation site i=10
   */
  public static class ExtendedFlowFunctionFactory extends DefaultFlowFunctionFactory {
    @Override
    public IForwardFlowFunction createForwardFlowFunction(
        FrameworkScope frameworkScope, BoomerangOptions options, ForwardBoomerangSolver<?> solver) {
      Strategies strategies = createStrategies(frameworkScope, options, solver);

      return new ExtendedForwardFlowFunction(strategies);
    }

    @Override
    public IBackwardFlowFunction createBackwardFlowFunction(
        FrameworkScope frameworkScope,
        BoomerangOptions options,
        BackwardBoomerangSolver<?> solver) {
      Strategies strategies = createStrategies(frameworkScope, options, solver);

      return new ExtendedBackwardFlowFunction(options.allocationSite(), strategies);
    }
  }

  private static class ExtendedForwardFlowFunction extends DefaultForwardFlowFunction {

    public ExtendedForwardFlowFunction(Strategies strategies) {
      super(strategies);
    }

    @Override
    public Set<State> normalFlow(ForwardQuery query, ControlFlowGraph.Edge nextEdge, Val fact) {
      Statement statement = nextEdge.getStart();
      if (statement.isAssignStmt() && statement.containsInvokeExpr()) {
        InvokeExpr invokeExpr = statement.getInvokeExpr();

        if (invokeExpr.getDeclaredMethod().toMethodWrapper().equals(STRING_VALUE_OF)) {
          Val arg = invokeExpr.getArg(0);

          if (arg.equals(fact)) {
            return Collections.singleton(new Node<>(nextEdge, statement.getLeftOp()));
          }
        }
      }

      return super.normalFlow(query, nextEdge, fact);
    }
  }

  private static class ExtendedBackwardFlowFunction extends DefaultBackwardFlowFunction {

    public ExtendedBackwardFlowFunction(IAllocationSite allocationSite, Strategies strategies) {
      super(allocationSite, strategies);
    }

    @Override
    public Collection<State> normalFlow(
        ControlFlowGraph.Edge currEdge, ControlFlowGraph.Edge nextEdge, Val fact) {
      Statement statement = nextEdge.getTarget();
      if (statement.isAssignStmt() && statement.containsInvokeExpr()) {
        if (statement.getLeftOp().equals(fact)) {
          InvokeExpr invokeExpr = statement.getInvokeExpr();

          if (invokeExpr.getDeclaredMethod().toMethodWrapper().equals(STRING_VALUE_OF)) {
            Val arg = invokeExpr.getArg(0);

            return Collections.singleton(new Node<>(nextEdge, arg));
          }
        }
      }

      return super.normalFlow(currEdge, nextEdge, fact);
    }
  }
}
