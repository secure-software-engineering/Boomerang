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
package boomerang.scope.wala;

import boomerang.scope.CallGraph;
import boomerang.scope.DataFlowScope;
import boomerang.scope.FrameworkScope;
import boomerang.scope.Method;
import boomerang.scope.Val;

public class WalaFrameworkScope implements FrameworkScope {

  @Override
  public Val getTrueValue(Method m) {
    throw new UnsupportedOperationException("implement me!");
  }

  @Override
  public Val getFalseValue(Method m) {
    throw new UnsupportedOperationException("implement me!");
  }

  @Override
  public CallGraph getCallGraph() {
    throw new UnsupportedOperationException("implement me!");
  }

  @Override
  public DataFlowScope getDataFlowScope() {
    throw new UnsupportedOperationException("implement me!");
  }
}
