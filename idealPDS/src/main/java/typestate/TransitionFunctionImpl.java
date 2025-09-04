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

import boomerang.scope.Statement;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.jspecify.annotations.NonNull;
import typestate.finiteautomata.Transition;
import typestate.finiteautomata.TransitionIdentity;
import typestate.finiteautomata.TransitionImpl;
import wpds.impl.Weight;

public class TransitionFunctionImpl implements TransitionFunction {

  @NonNull private final Multimap<Transition, Statement> stateChangeStatements;

  @NonNull private final Multimap<Transition, StatementSequence> stateChangeSequences;
  @NonNull private final Statement lastStateChangeStatement;

  public TransitionFunctionImpl(
      @NonNull Transition transition, @NonNull Statement stateChangeStatement) {
    this.stateChangeStatements = ImmutableMultimap.of(transition, stateChangeStatement);

    this.stateChangeSequences = ImmutableMultimap.of(transition, new StatementSequence(stateChangeStatement));
    this.lastStateChangeStatement = stateChangeStatement;
  }

  public TransitionFunctionImpl(
      @NonNull Collection<Transition> transitions, @NonNull Statement stateChangeStatement) {
    Multimap<Transition, Statement> statementsMap = HashMultimap.create();
    for (Transition transition : transitions) {
      statementsMap.put(transition, stateChangeStatement);
    }

    this.stateChangeStatements = ImmutableMultimap.copyOf(statementsMap);

    Multimap<Transition, StatementSequence> sequencesMap = HashMultimap.create();
    for (Transition transition : transitions) {
      sequencesMap.put(transition, new StatementSequence(stateChangeStatement));
    }

    this.stateChangeSequences = ImmutableMultimap.copyOf(sequencesMap);
    this.lastStateChangeStatement = stateChangeStatement;
  }

  public TransitionFunctionImpl(
      @NonNull Multimap<Transition, Statement> transitionToStateChangeStatements,
      @NonNull Multimap<Transition, StatementSequence> transitionStatementSequences,
      @NonNull Statement stateChangeStatement) {
    this.stateChangeStatements = ImmutableMultimap.copyOf(transitionToStateChangeStatements);

    this.stateChangeSequences = ImmutableMultimap.copyOf(transitionStatementSequences);
    this.lastStateChangeStatement = stateChangeStatement;
  }

  @Override
  @NonNull
  public Multimap<Transition, Statement> getStateChangeStatements() {
    return stateChangeStatements;
  }

  public Multimap<Transition, StatementSequence> getStateChangeSequences() {
    return stateChangeSequences;
  }

  public Statement getLastStateChangeStatement() {
    return lastStateChangeStatement;
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

    Multimap<Transition, StatementSequence> sequences = HashMultimap.create();
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

          Collection<StatementSequence> firstSequences = stateChangeSequences.get(first);
          for (StatementSequence sequence : firstSequences) {
            List<Statement> statementList = new ArrayList<>(sequence.getSequence());
            statementList.add(func.getLastStateChangeStatement());

            sequences.put(transition, new StatementSequence(statementList));
          }
        }
      }
    }
    return new TransitionFunctionImpl(result, sequences, func.lastStateChangeStatement);
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
      Multimap<Transition, Statement> transitions = HashMultimap.create(stateChangeStatements);
      for (Transition t : stateChangeStatements.keySet()) {
        Transition idTransition = new TransitionImpl(t.from(), t.from());
        Collection<Statement> statements = stateChangeStatements.get(t);

        transitions.putAll(idTransition, statements);
      }

      return new TransitionFunctionImpl(transitions, ImmutableMultimap.of(), this.lastStateChangeStatement);
    }

    TransitionFunctionImpl func = (TransitionFunctionImpl) other;

    Multimap<Transition, Statement> result = HashMultimap.create();
    result.putAll(stateChangeStatements);
    result.putAll(func.getStateChangeStatements());

    Multimap<Transition, StatementSequence> sequences = HashMultimap.create();
    sequences.putAll(stateChangeSequences);
    sequences.putAll(func.stateChangeSequences);

    return new TransitionFunctionImpl(result, sequences, func.lastStateChangeStatement);
  }

  @Override
  public String toString() {
    return "Weight: " + stateChangeStatements.keySet();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    TransitionFunctionImpl that = (TransitionFunctionImpl) o;
    return Objects.equals(stateChangeStatements, that.stateChangeStatements) && Objects.equals(stateChangeSequences, that.stateChangeSequences) && Objects.equals(lastStateChangeStatement, that.lastStateChangeStatement);
  }

  @Override
  public int hashCode() {
    return Objects.hash(stateChangeStatements, stateChangeSequences, lastStateChangeStatement);
  }
}
