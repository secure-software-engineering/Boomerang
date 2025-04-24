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
package test.setup;

import boomerang.scope.DataFlowScope;
import boomerang.scope.FrameworkScope;
import boomerang.scope.Method;
import boomerang.utils.MethodWrapper;
import java.util.List;

public interface TestSetup {

  void initialize(
      String classPath,
      MethodWrapper methodWrapper,
      List<String> includedPackages,
      List<String> excludedPackages);

  Method getTestMethod();

  FrameworkScope createFrameworkScope(DataFlowScope dataFlowScope);
}
