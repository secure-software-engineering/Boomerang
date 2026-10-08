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
import boomerang.scope.Val;
import org.jspecify.annotations.Nullable;

public abstract class SuccessorListener {
  private final Statement curr;
  private final @Nullable Val fact;

  public SuccessorListener(Statement curr) {
    this(curr, null);
  }

  public SuccessorListener(Statement curr, @Nullable Val fact) {
    this.curr = curr;
    this.fact = fact;
  }

  public Statement getCurr() {
    return curr;
  }

  /**
   * @return the data-flow fact that is propagated to the successors, if known
   */
  public @Nullable Val getFact() {
    return fact;
  }

  public abstract void getSuccessor(Statement succ);
}
