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
package chains;

import boomerang.WeightedForwardQuery;
import boomerang.scope.ControlFlowGraph;
import java.util.Collection;
import java.util.Collections;
import typestate.TransitionFunction;
import typestate.finiteautomata.MatcherTransition;
import typestate.finiteautomata.State;
import typestate.finiteautomata.TypeStateMachineWeightFunctions;

public class ChainStateMachine extends TypeStateMachineWeightFunctions {

  public enum States implements State {
    INIT,
    CHAIN1,
    CHAIN2;

    @Override
    public boolean isErrorState() {
      return this != CHAIN2;
    }

    @Override
    public boolean isInitialState() {
      return this == INIT;
    }

    @Override
    public boolean isAccepting() {
      return this == CHAIN2;
    }
  }

  public ChainStateMachine() {
    addTransition(
        new MatcherTransition(
            States.INIT,
            ".*chain1.*",
            MatcherTransition.Parameter.This,
            States.CHAIN1,
            MatcherTransition.Type.OnCallToReturn));
    addTransition(
        new MatcherTransition(
            States.CHAIN1,
            ".chain1.*",
            MatcherTransition.Parameter.This,
            States.CHAIN1,
            MatcherTransition.Type.OnCallToReturn));
    addTransition(
        new MatcherTransition(
            States.CHAIN1,
            ".*chain2.*",
            MatcherTransition.Parameter.This,
            States.CHAIN2,
            MatcherTransition.Type.OnCallToReturn));
    addTransition(
        new MatcherTransition(
            States.CHAIN2,
            ".chain1.*",
            MatcherTransition.Parameter.This,
            States.CHAIN1,
            MatcherTransition.Type.OnCallToReturn));
    addTransition(
        new MatcherTransition(
            States.CHAIN2,
            ".chain2.*",
            MatcherTransition.Parameter.This,
            States.CHAIN2,
            MatcherTransition.Type.OnCallToReturn));
  }

  @Override
  public Collection<WeightedForwardQuery<TransitionFunction>> generateSeed(
      ControlFlowGraph.Edge stmt) {
    try {
      return generateAtAllocationSiteOf(stmt, Class.forName(Chain.class.getName()));
    } catch (ClassNotFoundException e) {
      return Collections.emptySet();
    }
  }

  @Override
  protected State initialState() {
    return States.INIT;
  }
}
