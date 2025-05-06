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

public class TransitionFunctionZero implements TransitionFunction {

  @NonNull private static final TransitionFunctionZero zero = new TransitionFunctionZero();

  private TransitionFunctionZero() {}

  @NonNull
  public static TransitionFunctionZero zero() {
    return zero;
  }

  @NonNull
  @Override
  public Collection<Transition> getValues() {
    throw new IllegalStateException("TransitionZero.getValues() - don't");
  }

  @NonNull
  @Override
  public Set<ControlFlowGraph.Edge> getStateChangeStatements() {
    throw new IllegalStateException("This should not happen!");
  }

  @NonNull
  @Override
  public Weight extendWith(@NonNull Weight other) {
    if (other.equals(one())) return this;
    if (this.equals(one())) return other;
    if (other.equals(zero()) || this.equals(zero())) {
      return zero();
    }
    TransitionFunctionImpl func = (TransitionFunctionImpl) other;
    Set<? extends Transition> otherTransitions = (Set<? extends Transition>) func.getValues();
    Set<Transition> ress = new HashSet<>();
    Set<ControlFlowGraph.Edge> newStateChangeStatements = new HashSet<>();
    for (Transition first : getValues()) {
      for (Transition second : otherTransitions) {

        if (second.equals(TransitionIdentity.identity())) {
          ress.add(first);
          newStateChangeStatements.addAll(getStateChangeStatements());
        } else if (first.equals(TransitionIdentity.identity())) {
          ress.add(second);
          newStateChangeStatements.addAll(func.getStateChangeStatements());
        } else if (first.to().equals(second.from())) {
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
    if (this.equals(zero())) return other;
    if (other.equals(zero())) return this;
    if (other.equals(one()) && this.equals(one())) {
      return one();
    }

    TransitionFunctionImpl func = (TransitionFunctionImpl) other;
    if (other.equals(one()) || this.equals(one())) {
      Set<Transition> transitions =
          new HashSet<>((other.equals(one()) ? getValues() : func.getValues()));
      Set<Transition> idTransitions = Sets.newHashSet();
      for (Transition t : transitions) {
        idTransitions.add(new TransitionImpl(t.from(), t.from()));
      }
      transitions.addAll(idTransitions);
      return new TransitionFunctionImpl(
          transitions,
          Sets.newHashSet(
              (other.equals(one())
                  ? getStateChangeStatements()
                  : func.getStateChangeStatements())));
    }
    Set<Transition> transitions = new HashSet<>(func.getValues());
    transitions.addAll(getValues());
    HashSet<ControlFlowGraph.Edge> newStateChangeStmts =
        Sets.newHashSet(getStateChangeStatements());
    newStateChangeStmts.addAll(func.getStateChangeStatements());
    return new TransitionFunctionImpl(transitions, newStateChangeStmts);
  }

  public String toString() {
    return "ZERO";
  }
}
