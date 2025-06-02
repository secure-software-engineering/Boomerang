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
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import org.jspecify.annotations.NonNull;
import typestate.finiteautomata.Transition;
import typestate.finiteautomata.TransitionIdentity;
import typestate.finiteautomata.TransitionImpl;
import wpds.impl.Weight;

public class TransitionFunctionImpl implements TransitionFunction {

  @NonNull private final Multimap<Transition, Statement> stateChangeStatements;

  public TransitionFunctionImpl(
      @NonNull Transition transition, @NonNull Statement stateChangeStatement) {
    this.stateChangeStatements = ImmutableMultimap.of(transition, stateChangeStatement);
  }

  public TransitionFunctionImpl(
      @NonNull Collection<Transition> transitions, @NonNull Statement stateChangeStatement) {
    Multimap<Transition, Statement> map = HashMultimap.create();
    for (Transition transition : transitions) {
      map.put(transition, stateChangeStatement);
    }

    this.stateChangeStatements = ImmutableMultimap.copyOf(map);
  }

  public TransitionFunctionImpl(
      @NonNull Multimap<Transition, Statement> transitionToStateChangeStatements) {
    this.stateChangeStatements = ImmutableMultimap.copyOf(transitionToStateChangeStatements);
  }

  @Override
  @NonNull
  public Multimap<Transition, Statement> getStateChangeStatements() {
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
    Multimap<Transition, Statement> result = HashMultimap.create();
    Set<Transition> ress = new HashSet<>();
    Set<Edge> newStateChangeStatements = new HashSet<>();
    for (Transition first : stateChangeStatements.keySet()) {
      for (Transition second : func.stateChangeStatements.keySet()) {

        TransitionIdentity tIdentity = TransitionIdentity.identity();
        if (second == tIdentity) {
          Collection<Statement> statements = stateChangeStatements.get(first);
          result.putAll(first, statements);
        } else if (first == tIdentity) {
          Collection<Statement> statements = func.stateChangeStatements.get(second);
          result.putAll(second, statements);
        } else if (first.to().equals(second.from())) {
          Transition transition = new TransitionImpl(first.from(), second.to());
          Collection<Statement> statements = func.stateChangeStatements.get(second);
          result.putAll(transition, statements);
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
      Multimap<Transition, Statement> transitions = HashMultimap.create(stateChangeStatements);
      for (Transition t : stateChangeStatements.keySet()) {
        Transition idTransition = new TransitionImpl(t.from(), t.from());
        Collection<Statement> statements = stateChangeStatements.get(t);

        transitions.putAll(idTransition, statements);
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

    Multimap<Transition, Statement> result = HashMultimap.create();
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
