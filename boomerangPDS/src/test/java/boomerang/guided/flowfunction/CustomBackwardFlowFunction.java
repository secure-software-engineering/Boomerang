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
package boomerang.guided.flowfunction;

import boomerang.flowfunction.DefaultBackwardFlowFunction;
import boomerang.flowfunction.DefaultBackwardFlowFunctionOptions;
import boomerang.scope.ControlFlowGraph.Edge;
import boomerang.scope.DeclaredMethod;
import boomerang.scope.Method;
import boomerang.scope.Statement;
import boomerang.scope.Val;
import java.util.Collection;
import java.util.Collections;
import wpds.interfaces.State;

public class CustomBackwardFlowFunction extends DefaultBackwardFlowFunction {

  public CustomBackwardFlowFunction(DefaultBackwardFlowFunctionOptions options) {
    super(options);
  }

  @Override
  public Collection<State> normalFlow(Edge currEdge, Edge nextEdge, Val fact) {
    if (nextEdge.getTarget().containsInvokeExpr()) {
      DeclaredMethod method = nextEdge.getTarget().getInvokeExpr().getDeclaredMethod();
      // Avoid any propagations by passing the call site (also when the fact is not used at the call
      // site).
      if (method.getDeclaringClass().getFullyQualifiedName().equals("java.lang.System")
          && method.getName().equals("exit")) {
        return Collections.emptySet();
      }
    }
    return super.normalFlow(currEdge, nextEdge, fact);
  }

  @Override
  public Collection<State> callToReturnFlow(Edge currEdge, Edge nextEdge, Val fact) {
    if (nextEdge.getTarget().containsInvokeExpr()) {
      DeclaredMethod method = nextEdge.getTarget().getInvokeExpr().getDeclaredMethod();
      // Avoid any propagations by passing the call site (also when the fact is not used at the call
      // site).
      if (method.getDeclaringClass().getFullyQualifiedName().equals("java.lang.System")
          && method.getName().equals("exit")) {
        return Collections.emptySet();
      }
    }
    return super.callToReturnFlow(currEdge, nextEdge, fact);
  }

  @Override
  public Collection<Val> callFlow(Statement callSite, Val fact, Method callee, Statement calleeSp) {
    // Avoid propagations into the method when a call parameter reaches the call site
    if (callee.getDeclaringClass().getFullyQualifiedName().equals("java.lang.System")
        && callee.getName().equals("exit")) {
      return Collections.emptySet();
    }
    return super.callFlow(callSite, fact, callee, calleeSp);
  }
}
