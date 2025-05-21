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
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import org.jspecify.annotations.NonNull;
import typestate.finiteautomata.Transition;
import typestate.finiteautomata.TransitionIdentity;
import typestate.finiteautomata.TransitionImpl;
import wpds.impl.Weight;

public class TransitionFunctionImpl implements TransitionFunction {

  @NonNull private final Set<? extends Transition> values;
  @NonNull private final Set<Edge> stateChangeStatements;

  public TransitionFunctionImpl(
      @NonNull Set<? extends Transition> trans, @NonNull Set<Edge> stateChangeStatements) {
    this.stateChangeStatements = stateChangeStatements;
    this.values = trans;
  }

  public TransitionFunctionImpl(
      @NonNull Transition trans, @NonNull Set<Edge> stateChangeStatements) {
    this(Collections.singleton(trans), stateChangeStatements);
  }

  @Override
  @NonNull
  public Collection<Transition> getValues() {
    return new HashSet<>(values);
  }

  @NonNull
  @Override
  public Set<Edge> getStateChangeStatements() {
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
    Set<Transition> ress = new HashSet<>();
    Set<Edge> newStateChangeStatements = new HashSet<>();
    for (Transition first : values) {
      for (Transition second : func.values) {

        TransitionIdentity tIdentity = TransitionIdentity.identity();
        if (second == tIdentity) {
          ress.add(first);
          newStateChangeStatements.addAll(stateChangeStatements);
        } else if (first == tIdentity) {
          ress.add(second);
          newStateChangeStatements.addAll(func.stateChangeStatements);
        } else if (first.to().equals(second.from())) {
          ress.add(new TransitionImpl(first.from(), second.to()));
          newStateChangeStatements.addAll(func.stateChangeStatements);
        }
      }
    }
    return new TransitionFunctionImpl(ress, newStateChangeStatements);
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
      Set<Transition> transitions = new HashSet<>(values);
      Set<Transition> idTransitions = new HashSet<>();
      for (Transition t : transitions) {
        idTransitions.add(new TransitionImpl(t.from(), t.from()));
      }
      transitions.addAll(idTransitions);
      return new TransitionFunctionImpl(transitions, stateChangeStatements);
    }

    TransitionFunction func = (TransitionFunction) other;
    Set<Transition> transitions = new HashSet<>(func.getValues());
    transitions.addAll(values);
    Set<Edge> newStateChangeStmts = new HashSet<>(stateChangeStatements);
    newStateChangeStmts.addAll(func.getStateChangeStatements());
    return new TransitionFunctionImpl(transitions, newStateChangeStmts);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    TransitionFunctionImpl that = (TransitionFunctionImpl) o;
    return Objects.equals(values, that.values);
  }

  @Override
  public int hashCode() {
    return Objects.hash(values);
  }

  @Override
  public String toString() {
    return "Weight: " + values;
  }
}
