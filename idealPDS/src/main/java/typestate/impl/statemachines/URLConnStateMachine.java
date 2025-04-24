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
package typestate.impl.statemachines;

import boomerang.WeightedForwardQuery;
import boomerang.scope.ControlFlowGraph.Edge;
import java.util.Collection;
import typestate.TransitionFunction;
import typestate.finiteautomata.MatcherTransition;
import typestate.finiteautomata.MatcherTransition.Parameter;
import typestate.finiteautomata.MatcherTransition.Type;
import typestate.finiteautomata.State;
import typestate.finiteautomata.TypeStateMachineWeightFunctions;

public class URLConnStateMachine extends TypeStateMachineWeightFunctions {

  private static final String CONNECT_METHOD = ".*connect.*";
  private static final String TYPE = "java.net.URLConnection";
  private static final String ILLEGAL_OPERATIONS =
      ".*(setDoInput|setDoOutput|setAllowUserInteraction|setUseCaches|setIfModifiedSince|setRequestProperty|addRequestProperty|getRequestProperty|getRequestProperties).*";

  public enum States implements State {
    INIT,
    CONNECTED,
    ERROR;

    @Override
    public boolean isErrorState() {
      return this == ERROR;
    }

    @Override
    public boolean isInitialState() {
      return this == INIT;
    }

    @Override
    public boolean isAccepting() {
      return this == CONNECTED;
    }
  }

  public URLConnStateMachine() {
    addTransition(
        new MatcherTransition(
            States.INIT, CONNECT_METHOD, Parameter.This, States.CONNECTED, Type.OnCall));
    addTransition(
        new MatcherTransition(
            States.CONNECTED, ILLEGAL_OPERATIONS, Parameter.This, States.ERROR, Type.OnCall));
    addTransition(
        new MatcherTransition(
            States.ERROR, ILLEGAL_OPERATIONS, Parameter.This, States.ERROR, Type.OnCall));
  }

  @Override
  public Collection<WeightedForwardQuery<TransitionFunction>> generateSeed(Edge unit) {
    return this.generateThisAtAnyCallSitesOf(unit, TYPE, CONNECT_METHOD);
  }

  @Override
  protected State initialState() {
    return States.CONNECTED;
  }
}
