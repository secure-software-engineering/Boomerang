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
package boomerang.scope.soot.jimple;

import boomerang.scope.ControlFlowGraph;
import boomerang.scope.Field;
import boomerang.scope.Method;
import boomerang.scope.StaticFieldVal;
import boomerang.scope.WrappedClass;

public class JimpleStaticFieldVal extends StaticFieldVal {

  public JimpleStaticFieldVal(WrappedClass declaringClass, Field field, Method method) {
    this(declaringClass, field, method, null);
  }

  private JimpleStaticFieldVal(
      WrappedClass declaringClass, Field field, Method method, ControlFlowGraph.Edge unbalanced) {
    super(declaringClass, field, method, unbalanced);
  }
}
