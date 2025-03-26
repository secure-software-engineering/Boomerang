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
package typestate;

import static typestate.TransitionFunctionZero.zero;

import boomerang.scope.ControlFlowGraph;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.jspecify.annotations.NonNull;
import typestate.finiteautomata.Transition;
import typestate.finiteautomata.TransitionIdentity;
import typestate.finiteautomata.TransitionImpl;
import wpds.impl.Weight;

public class TransitionFunctionOne implements TransitionFunction {

  @NonNull private static final TransitionFunctionOne one = new TransitionFunctionOne();

  public TransitionFunctionOne() {}

  public static TransitionFunctionOne one() {
    return one;
  }

  @NonNull
  @Override
  public Collection<Transition> getValues() {
    throw new IllegalStateException("TransitionFunctionOne.getValues() - don't");
  }

  @NonNull
  @Override
  public Set<ControlFlowGraph.Edge> getStateChangeStatements() {
    throw new IllegalStateException(
        "TransitionFunctionOne.getStateChangeStatements() - This should not happen!");
  }

  @NonNull
  @Override
  public Weight extendWith(@NonNull Weight other) {
    TransitionFunctionOne one1 = one();
    if (other == (one1)) return this;
    if (this == (one1)) return other;
    TransitionFunction func = (TransitionFunction) other;
    Set<? extends Transition> otherTransitions = (Set<? extends Transition>) func.getValues();
    Set<Transition> ress = new HashSet<>();
    Set<ControlFlowGraph.Edge> newStateChangeStatements = new HashSet<>();
    for (Transition first : getValues()) {
      for (Transition second : otherTransitions) {

        if (second == (TransitionIdentity.identity())) {
          ress.add(first);
          newStateChangeStatements.addAll(getStateChangeStatements());
        } else if (first == (TransitionIdentity.identity())) {
          ress.add(second);
          newStateChangeStatements.addAll(func.getStateChangeStatements());
        } else if (first.to() == (second.from())) {
          ress.add(new TransitionImpl(first.from(), second.to()));
          newStateChangeStatements.addAll(func.getStateChangeStatements());
        }
      }
    }
    return new TransitionFunctionImpl(ress, newStateChangeStatements);
  }

  @NonNull
  @Override
  public Weight combineWith(@NonNull Weight other) {
    if (!(other instanceof TransitionFunction)) {
      throw new RuntimeException();
    }
    TransitionFunctionZero zero = zero();
    TransitionFunction one1 = one();

    if (other == (zero)) return this;

    if (other == one1 && this == one1) {
      return one1;
    }

    TransitionFunction func = (TransitionFunction) other;
    if (other == (one1) || this == (one1)) {
      Set<Transition> transitions =
          new HashSet<>((other == (one1) ? getValues() : func.getValues()));
      Set<Transition> idTransitions = Sets.newHashSet();
      for (Transition t : transitions) {
        idTransitions.add(new TransitionImpl(t.from(), t.from()));
      }
      transitions.addAll(idTransitions);
      return new TransitionFunctionImpl(
          transitions,
          Sets.newHashSet(
              (other == (one1) ? getStateChangeStatements() : func.getStateChangeStatements())));

    }
    Set<Transition> transitions = new HashSet<>(func.getValues());
    transitions.addAll(getValues());
    HashSet<ControlFlowGraph.Edge> newStateChangeStmts =
        Sets.newHashSet(getStateChangeStatements());
    newStateChangeStmts.addAll(func.getStateChangeStatements());
    return new TransitionFunctionImpl(transitions, newStateChangeStmts);
  }

  public String toString() {
    return "ONE";
  }
}
