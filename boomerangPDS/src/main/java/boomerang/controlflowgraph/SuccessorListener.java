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
package boomerang.controlflowgraph;

import boomerang.scope.Statement;

public abstract class SuccessorListener {
  private final Statement curr;

  public SuccessorListener(Statement curr) {
    this.curr = curr;
  }

  public Statement getCurr() {
    return curr;
  }

  public abstract void getSuccessor(Statement succ);
}
