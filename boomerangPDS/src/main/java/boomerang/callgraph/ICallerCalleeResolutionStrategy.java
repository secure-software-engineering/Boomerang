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
package boomerang.callgraph;

import boomerang.WeightedBoomerang;
import boomerang.scope.CallGraph;
import boomerang.scope.InvokeExpr;
import boomerang.scope.Method;
import boomerang.scope.Statement;
import java.util.Collection;

public interface ICallerCalleeResolutionStrategy {

  interface Factory {
    ICallerCalleeResolutionStrategy newInstance(WeightedBoomerang solver, CallGraph cg);
  }

  void computeFallback(ObservableDynamicICFG observableDynamicICFG);

  Method resolveSpecialInvoke(InvokeExpr ie);

  Collection<Method> resolveInstanceInvoke(Statement stmt);

  Method resolveStaticInvoke(InvokeExpr ie);
}
