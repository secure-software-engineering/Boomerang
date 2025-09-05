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
import java.util.List;
import java.util.Objects;
import org.jspecify.annotations.NonNull;
import typestate.finiteautomata.Transition;
import typestate.finiteautomata.TransitionIdentity;
import typestate.finiteautomata.TransitionImpl;
import wpds.impl.Weight;

public class TransitionFunctionImpl implements TransitionFunction {

  @NonNull private final Multimap<Transition, StatementSequence> stateChangeSequences;
  @NonNull private final Statement lastStateChangeStatement;

  public TransitionFunctionImpl(
      @NonNull Transition transition, @NonNull Statement stateChangeStatement) {
    this.stateChangeSequences =
        ImmutableMultimap.of(
            transition,
            new StatementSequence(new StatementSequence.Entry(stateChangeStatement, transition)));
    this.lastStateChangeStatement = stateChangeStatement;
  }

  public TransitionFunctionImpl(
      @NonNull Collection<Transition> transitions, @NonNull Statement stateChangeStatement) {
    Multimap<Transition, StatementSequence> sequencesMap = HashMultimap.create();
    for (Transition transition : transitions) {
      sequencesMap.put(
          transition,
          new StatementSequence(new StatementSequence.Entry(stateChangeStatement, transition)));
    }

    this.stateChangeSequences = ImmutableMultimap.copyOf(sequencesMap);
    this.lastStateChangeStatement = stateChangeStatement;
  }

  public TransitionFunctionImpl(
      @NonNull Multimap<Transition, StatementSequence> transitionStatementSequences,
      @NonNull Statement stateChangeStatement) {
    this.stateChangeSequences = ImmutableMultimap.copyOf(transitionStatementSequences);
    this.lastStateChangeStatement = stateChangeStatement;
  }

  @NonNull
  @Override
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
    Multimap<Transition, StatementSequence> result = HashMultimap.create();
    for (Transition first : stateChangeSequences.keySet()) {
      for (Transition second : func.stateChangeSequences.keySet()) {

        TransitionIdentity tIdentity = TransitionIdentity.identity();
        if (second == tIdentity) {
          Collection<StatementSequence> sequences = stateChangeSequences.get(first);
          result.putAll(first, sequences);
        } else if (first == tIdentity) {
          Collection<StatementSequence> sequences = func.stateChangeSequences.get(second);
          result.putAll(second, sequences);
        } else if (first.to().equals(second.from())) {
          Transition transition = new TransitionImpl(first.from(), second.to());

          Collection<StatementSequence> sequences = stateChangeSequences.get(first);
          for (StatementSequence sequence : sequences) {
            List<StatementSequence.Entry> statementList = new ArrayList<>(sequence.getSequence());
            statementList.add(
                new StatementSequence.Entry(func.getLastStateChangeStatement(), second));

            result.put(transition, new StatementSequence(statementList));
          }
        }
      }
    }
    return new TransitionFunctionImpl(result, func.lastStateChangeStatement);
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
      Multimap<Transition, StatementSequence> transitions =
          HashMultimap.create(stateChangeSequences);
      for (Transition t : stateChangeSequences.keySet()) {
        Transition idTransition = new TransitionImpl(t.from(), t.from());
        Collection<StatementSequence> statements = stateChangeSequences.get(t);

        transitions.putAll(idTransition, statements);
      }

      return new TransitionFunctionImpl(transitions, this.lastStateChangeStatement);
    }

    TransitionFunctionImpl func = (TransitionFunctionImpl) other;

    Multimap<Transition, StatementSequence> sequences = HashMultimap.create();
    sequences.putAll(stateChangeSequences);
    sequences.putAll(func.stateChangeSequences);

    return new TransitionFunctionImpl(sequences, func.lastStateChangeStatement);
  }

  @Override
  public String toString() {
    return "Weight: " + stateChangeSequences.keySet();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    TransitionFunctionImpl that = (TransitionFunctionImpl) o;
    return Objects.equals(stateChangeSequences, that.stateChangeSequences)
        && Objects.equals(lastStateChangeStatement, that.lastStateChangeStatement);
  }

  @Override
  public int hashCode() {
    return Objects.hash(stateChangeSequences, lastStateChangeStatement);
  }
}
