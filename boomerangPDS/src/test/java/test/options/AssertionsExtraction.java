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
package test.options;

import boomerang.BackwardQuery;
import boomerang.Query;
import boomerang.scope.AnalysisScope;
import boomerang.scope.ControlFlowGraph;
import boomerang.scope.DeclaredMethod;
import boomerang.scope.FrameworkScope;
import boomerang.scope.InvokeExpr;
import boomerang.scope.Statement;
import boomerang.scope.Val;
import java.util.Collection;
import java.util.Collections;

public class AssertionsExtraction extends AnalysisScope {

  public AssertionsExtraction(FrameworkScope frameworkScope) {
    super(frameworkScope);
  }

  @Override
  protected Collection<? extends Query> generate(ControlFlowGraph.Edge edge) {
    Statement statement = edge.getTarget();

    if (statement.containsInvokeExpr()) {
      InvokeExpr invokeExpr = statement.getInvokeExpr();
      DeclaredMethod declaredMethod = invokeExpr.getDeclaredMethod();

      if (declaredMethod
          .getDeclaringClass()
          .getFullyQualifiedName()
          .equals(OptionAssertions.class.getName())) {
        if (declaredMethod.getName().equals(OptionAssertions.QUERY_FOR)) {
          Val arg = invokeExpr.getArg(0);
          BackwardQuery query = BackwardQuery.make(edge, arg);

          return Collections.singleton(query);
        }
      }
    }

    return Collections.emptySet();
  }
}
