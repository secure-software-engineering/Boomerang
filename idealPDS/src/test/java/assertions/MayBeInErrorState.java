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
package assertions;

import boomerang.scope.Statement;
import boomerang.scope.Val;
import java.util.Collection;
import typestate.finiteautomata.State;

public class MayBeInErrorState extends StateResult {

  private boolean satisfied;
  private boolean checked;

  public MayBeInErrorState(Statement statement, Val seed) {
    super(statement, seed);

    this.satisfied = false;
    this.checked = false;
  }

  @Override
  public boolean isUnsound() {
    return !checked || !satisfied;
  }

  @Override
  public void computedStates(Collection<State> states) {
    // Check if there is any error state
    for (State state : states) {
      satisfied |= state.isErrorState();
    }
    checked = true;
  }

  @Override
  public String getAssertedMessage() {
    if (checked) {
      return seed.getVariableName()
          + " is expected to be in an error state @ "
          + statement
          + " @ line "
          + statement.getLineNumber();
    } else {
      return statement + " @ line " + statement.getLineNumber() + " has not been checked";
    }
  }
}
