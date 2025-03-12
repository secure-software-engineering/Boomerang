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
package boomerang.flowfunction;

import boomerang.ForwardQuery;
import boomerang.scope.ControlFlowGraph.Edge;
import boomerang.scope.Field;
import boomerang.scope.Method;
import boomerang.scope.Statement;
import boomerang.scope.Val;
import boomerang.solver.ForwardBoomerangSolver;
import com.google.common.collect.Multimap;
import java.util.Collection;
import wpds.interfaces.State;

public interface IForwardFlowFunction {

  /**
   * Called by the forward analysis, when the forward solver reaches the returnStmt (any last
   * statement of callee method) of callee method with data-flow fact returnedVal.
   *
   * @param callee The method the data-flow analysis returns from. The caller method is not
   *     available, as it will be internally added by the framework.
   * @param returnStmt The statement from which the method returns from (will be any last/exit
   *     statement of the callee method)
   * @param returnedVal The data-flow fact that is returned.
   * @return A set of data-flow facts (Val) which will be propagated at any call site of the callee
   *     method.
   */
  Collection<Val> returnFlow(Method callee, Statement returnStmt, Val returnedVal);

  /**
   * Called by the forward analysis, when the forward solver reaches the callSite. Will be invoked
   * once per available callee (in the call graph).
   *
   * @param callSite A call site reached by the backward analysis.
   * @param factAtCallSite The data-flow fact reaching the callSite
   * @param callee The callee that may be invoked at the callSite
   * @return A set of data-flow facts (Val) that will be propagated at the entry statements of
   *     method callee
   */
  Collection<Val> callFlow(Statement callSite, Val factAtCallSite, Method callee);

  /**
   * Called by the forward analysis, for any non return statements or call site statements.
   *
   * <p>Note: The logic differs from general IFDS logic here. edge.getStart() can also contain a
   * call site, but fact is not used in the call site (no parameter or base variable of the call
   * expression) .
   *
   * @param edge The control-flow graph edge that will be propagated next.
   * @param fact The incoming data-flow fact that reaches the edge.
   * @return A set of data-flow states (states in the pushdown system).
   */
  Collection<State> normalFlow(ForwardQuery query, Edge edge, Val fact);

  /**
   * Called by the forward analysis, when data-flow by-passes a call site with data-flow fact. Here
   * logic can be added to handle native calls, or methods that are excluded from propagation. Note:
   * The logic differs from general IFDS logic here. callToReturn is _only_ invoked when fact is
   * also used in the call site (edge.getStart()), i.e. fact is a parameter or the base variable of
   * the call expression. As a consequence, special handling for call site may need to be
   * implemented as part of callToReturn and normalFlow.
   *
   * @param edge Edge that bypasses the call site. edge.getStart() is the call site,
   *     edge.getTarget() is any succsessor
   * @param fact The fact that by-passes the call site.
   * @return A set of data-flow states (states in the pushdown system)
   */
  Collection<State> callToReturnFlow(ForwardQuery query, Edge edge, Val fact);

  void setSolver(
      ForwardBoomerangSolver<?> solver,
      Multimap<Field, Statement> fieldLoadStatements,
      Multimap<Field, Statement> fieldStoreStatements);
}
