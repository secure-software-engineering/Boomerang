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
import java.util.HashSet;
import java.util.Objects;
import test.Assertion;
import typestate.TransitionFunction;
import typestate.finiteautomata.State;
import typestate.finiteautomata.Transition;

public abstract class StateResult implements Assertion {

  protected final Statement statement;
  protected final Val seed;

  public StateResult(Statement statement, Val seed) {
    this.statement = statement;
    this.seed = seed;
  }

  public Statement getStmt() {
    return statement;
  }

  public Val getSeed() {
    return seed;
  }

  public boolean isImprecise() {
    return false;
  }

  public abstract boolean isUnsound();

  public void computedResults(TransitionFunction function) {
    Collection<State> states = new HashSet<>();

    for (Transition transition : function.getStateChangeStatements().keySet()) {
      states.add(transition.to());
    }

    computedStates(states);
  }

  public abstract void computedStates(Collection<State> states);

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    StateResult that = (StateResult) o;
    return Objects.equals(statement, that.statement) && Objects.equals(seed, that.seed);
  }

  @Override
  public int hashCode() {
    return Objects.hash(statement, seed);
  }
}
