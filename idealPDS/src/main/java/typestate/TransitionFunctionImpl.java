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

import boomerang.scope.ControlFlowGraph.Edge;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.Nonnull;
import typestate.finiteautomata.Transition;
import typestate.finiteautomata.TransitionIdentity;
import typestate.finiteautomata.TransitionImpl;
import wpds.impl.Weight;

public class TransitionFunctionImpl implements TransitionFunction {
  @Nonnull private final Set<? extends Transition> values;
  @Nonnull private final Set<Edge> stateChangeStatements;

  public TransitionFunctionImpl(
      @Nonnull Set<? extends Transition> trans, @Nonnull Set<Edge> stateChangeStatements) {
    this.stateChangeStatements = stateChangeStatements;
    this.values = trans;
  }

  public TransitionFunctionImpl(
      @Nonnull Transition trans, @Nonnull Set<Edge> stateChangeStatements) {
    this(Collections.singleton(trans), stateChangeStatements);
  }

  @Override
  @Nonnull
  public Collection<Transition> getValues() {
    return Lists.newArrayList(values);
  }

  @Nonnull
  @Override
  public Set<Edge> getStateChangeStatements() {
    return stateChangeStatements;
  }

  @Nonnull
  @Override
  public Weight extendWith(@Nonnull Weight other) {
    final Weight one = TransitionFunctionOne.one();
    if (other == one) {
      return this;
    }
    final Weight zero = TransitionFunctionZero.zero();
    if (other == zero) {
      return zero;
    }
    TransitionFunctionImpl func = (TransitionFunctionImpl) other;
    Set<? extends Transition> otherTransitions = func.values;
    Set<Transition> ress = new HashSet<>();
    Set<Edge> newStateChangeStatements = new HashSet<>();
    for (Transition first : values) {
      for (Transition second : otherTransitions) {
        TransitionIdentity identity = TransitionIdentity.identity();
        if (second == identity) {
          ress.add(first);
          newStateChangeStatements.addAll(stateChangeStatements);
        } else if (first == identity) {
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

  @Nonnull
  @Override
  public Weight combineWith(@Nonnull Weight other) {
    if (!(other instanceof TransitionFunction)) {
      throw new RuntimeException();
    }
    if (other == TransitionFunctionZero.zero()) {
      return this;
    }
    Weight one = TransitionFunctionOne.one();
    TransitionFunction func = (TransitionFunction) other;
    Set<Transition> transitions = new HashSet<>(values);
    HashSet<Edge> newStateChangeStmts = Sets.newHashSet(stateChangeStatements);
    if (other == one) {
      Set<Transition> idTransitions = Sets.newHashSet();
      for (Transition t : transitions) {
        idTransitions.add(new TransitionImpl(t.from(), t.from()));
      }
      transitions.addAll(idTransitions);
    } else {
      transitions.addAll(func.getValues());
      newStateChangeStmts.addAll(func.getStateChangeStatements());
    }
    return new TransitionFunctionImpl(transitions, newStateChangeStmts);
  }

  public String toString() {
    return "Weight: " + values;
  }

  @Override
  public int hashCode() {
    return values.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    TransitionFunctionImpl other = (TransitionFunctionImpl) obj;
    return values.equals(other.values);
  }
}
