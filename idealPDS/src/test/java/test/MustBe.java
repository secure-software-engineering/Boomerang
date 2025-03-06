/**
 * ***************************************************************************** Copyright (c) 2018
 * Fraunhofer IEM, Paderborn, Germany. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * <p>SPDX-License-Identifier: EPL-2.0
 *
 * <p>Contributors: Johannes Spaeth - initial API and implementation
 * *****************************************************************************
 */
package test;

import boomerang.scope.Statement;
import boomerang.scope.Val;
import com.google.common.collect.Sets;
import java.util.Set;
import typestate.TransitionFunction;
import typestate.finiteautomata.Transition;
import typestate.finiteautomata.State;
import typestate.finiteautomata.TransitionIdentity;

public class MustBe extends ExpectedResults<TransitionFunction, Val> {

  MustBe(Statement unit, Val val, InternalState state) {
    super(unit, val, state);
  }

  @Override
  public void computedResults(TransitionFunction val) {
    Set<State> states = Sets.newHashSet();
    TransitionIdentity identity = TransitionIdentity.identity();
    for (Transition t : val.getValues()) {
      if (t != identity) {
        states.add(t.to());
      }
    }
    for (State s : states) {
        switch (state) {
            case ACCEPTING:
                satisfied |= !s.isErrorState() && states.size() == 1;
                break;
            case ERROR:
                satisfied |= s.isErrorState() && states.size() == 1;
                break;
        }
    }
  }

  public String toString() {
    return "MustBe " + super.toString();
  }

}
