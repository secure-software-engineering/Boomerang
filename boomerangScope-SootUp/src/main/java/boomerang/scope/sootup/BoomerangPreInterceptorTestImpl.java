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
package boomerang.scope.sootup;

import org.jspecify.annotations.NonNull;
import sootup.core.jimple.common.stmt.Stmt;

public class BoomerangPreInterceptorTestImpl extends BoomerangPreInterceptor {
  public BoomerangPreInterceptorTestImpl() {}

  public BoomerangPreInterceptorTestImpl(boolean transformConstantsSettings) {
    super(transformConstantsSettings);
  }

  protected boolean filterTransformableInvokeExprs(@NonNull Stmt stmt) {
    return !stmt.toString().contains("test.assertions.Assertions:")
        && !stmt.toString().contains("intQueryFor");
  }
}
