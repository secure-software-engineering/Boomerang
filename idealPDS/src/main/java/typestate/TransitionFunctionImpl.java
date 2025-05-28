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
package typestate;

import static typestate.TransitionFunctionOne.one;
import static typestate.TransitionFunctionZero.zero;

import boomerang.scope.ControlFlowGraph.Edge;
import boomerang.scope.Statement;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.jspecify.annotations.NonNull;
import typestate.finiteautomata.Transition;
import typestate.finiteautomata.TransitionIdentity;
import typestate.finiteautomata.TransitionImpl;
import wpds.impl.Weight;

public class TransitionFunctionImpl implements TransitionFunction {

  @NonNull private final Map<Transition, Statement> stateChangeStatements;

  public TransitionFunctionImpl(
      @NonNull Transition transition, @NonNull Statement stateChangeStatement) {
    this.stateChangeStatements = Map.of(transition, stateChangeStatement);
  }

  public TransitionFunctionImpl(
      @NonNull Collection<Transition> transitions, @NonNull Statement stateChangeStatement) {
    Map<Transition, Statement> map = new HashMap<>();
    for (Transition transition : transitions) {
      map.put(transition, stateChangeStatement);
    }

    this.stateChangeStatements = Map.copyOf(map);
  }

  public TransitionFunctionImpl(
      @NonNull Map<Transition, Statement> transitionToStateChangeStatements) {
    this.stateChangeStatements = Map.copyOf(transitionToStateChangeStatements);
  }

  @Override
  @NonNull
  public Map<Transition, Statement> getStateChangeStatements() {
    return stateChangeStatements;
  }

  @NonNull
  @Override
  public Weight extendWith(@NonNull Weight other) {
    if (other == one()) {
      return this;
    }
    if (other == zero()) {
      return zero();
    }
    TransitionFunctionImpl func = (TransitionFunctionImpl) other;
    Map<Transition, Statement> result = new HashMap<>();
    Set<Transition> ress = new HashSet<>();
    Set<Edge> newStateChangeStatements = new HashSet<>();
    for (Transition first : stateChangeStatements.keySet()) {
      for (Transition second : func.stateChangeStatements.keySet()) {

        TransitionIdentity tIdentity = TransitionIdentity.identity();
        if (second == tIdentity) {
          Statement statement = stateChangeStatements.get(first);
          result.put(first, statement);
        } else if (first == tIdentity) {
          Statement statement = func.stateChangeStatements.get(second);
          result.put(second, statement);
        } else if (first.to().equals(second.from())) {
          Transition transition = new TransitionImpl(first.from(), second.to());
          Statement statement = func.stateChangeStatements.get(second);
          result.put(transition, statement);
          // ress.add(new TransitionImpl(first.from(), second.to()));
          // newStateChangeStatements.addAll(func.stateChangeStatementsOld);
        }
      }
    }
    return new TransitionFunctionImpl(result);
  }

  @NonNull
  @Override
  public Weight combineWith(@NonNull Weight other) {
    if (!(other instanceof TransitionFunction)) {
      throw new RuntimeException("Cannot combine TransitionFunction with non TransitionFunction");
    }

    if (other == zero()) {
      return this;
    }

    if (other == one()) {
      /*Set<Transition> transitions = new HashSet<>(values);
      Set<Transition> idTransitions = new HashSet<>();
      for (Transition t : transitions) {
        idTransitions.add(new TransitionImpl(t.from(), t.from()));
      }
      transitions.addAll(idTransitions);
      return new TransitionFunctionImpl(transitions, stateChangeStatementsOld);*/
      Map<Transition, Statement> transitions = new HashMap<>(stateChangeStatements);
      for (Transition t : stateChangeStatements.keySet()) {
        Transition idTransition = new TransitionImpl(t.from(), t.from());
        Statement statement = stateChangeStatements.get(t);

        transitions.put(idTransition, statement);
      }

      return new TransitionFunctionImpl(transitions);
    }

    /*TransitionFunction func = (TransitionFunction) other;
    Set<Transition> transitions = new HashSet<>(func.getValues());
    transitions.addAll(values);
    Set<Edge> newStateChangeStmts = new HashSet<>(stateChangeStatementsOld);
    newStateChangeStmts.addAll(func.getStateChangeStatementsOld());
    return new TransitionFunctionImpl(transitions, newStateChangeStmts);*/
    TransitionFunction func = (TransitionFunction) other;

    Map<Transition, Statement> result = new HashMap<>();
    result.putAll(stateChangeStatements);
    result.putAll(func.getStateChangeStatements());

    return new TransitionFunctionImpl(result);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    TransitionFunctionImpl that = (TransitionFunctionImpl) o;
    return Objects.equals(stateChangeStatements, that.stateChangeStatements);
  }

  @Override
  public int hashCode() {
    return Objects.hash(stateChangeStatements);
  }

  @Override
  public String toString() {
    return "Weight: " + stateChangeStatements.keySet();
  }
}
