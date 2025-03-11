/**
 * ***************************************************************************** 
 * Copyright (c) 2025 Fraunhofer IEM, Paderborn, Germany. This program and the
 * accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at http://www.eclipse.org/legal/epl-2.0.
 *
 * <p>SPDX-License-Identifier: EPL-2.0
 *
 * <p>Contributors: Johannes Spaeth - initial API and implementation
 * *****************************************************************************
 */
package typestate.impl.statemachines;

import boomerang.WeightedForwardQuery;
import boomerang.scope.ControlFlowGraph.Edge;
import java.util.Collection;
import java.util.Collections;
import typestate.TransitionFunction;
import typestate.finiteautomata.MatcherTransition;
import typestate.finiteautomata.MatcherTransition.Parameter;
import typestate.finiteautomata.MatcherTransition.Type;
import typestate.finiteautomata.State;
import typestate.finiteautomata.TypeStateMachineWeightFunctions;

public class FileMustBeClosedStateMachineCallToReturn extends TypeStateMachineWeightFunctions {

  public enum States implements State {
    INIT,
    OPENED,
    CLOSED;

    @Override
    public boolean isErrorState() {
      return this == OPENED;
    }

    @Override
    public boolean isInitialState() {
      return this == INIT;
    }

    @Override
    public boolean isAccepting() {
      return this == CLOSED;
    }
  }

  public FileMustBeClosedStateMachineCallToReturn() {
    addTransition(
        new MatcherTransition(
            States.INIT, ".*open.*", Parameter.This, States.OPENED, Type.OnCallToReturn));
    addTransition(
        new MatcherTransition(
            States.INIT, ".*close.*", Parameter.This, States.CLOSED, Type.OnCallToReturn));
    addTransition(
        new MatcherTransition(
            States.OPENED, ".*close.*", Parameter.This, States.CLOSED, Type.OnCallToReturn));
  }

  @Override
  public State initialState() {
    return States.INIT;
  }

  @Override
  public Collection<WeightedForwardQuery<TransitionFunction>> generateSeed(Edge edge) {
    try {
      return generateAtAllocationSiteOf(edge, Class.forName("typestate.targets.helper.File"));
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }
    return Collections.emptySet();
  }
}
