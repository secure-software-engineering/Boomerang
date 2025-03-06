/**
 * ***************************************************************************** Copyright (c) 2018
 * Fraunhofer IEM, Paderborn, Germany. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
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
import typestate.finiteautomata.ITransition;
import typestate.finiteautomata.Transition;
import wpds.impl.Weight;

public class TransitionFunctionImpl implements TransitionFunction {
  @Nonnull private final Set<? extends ITransition> values;
  @Nonnull private final Set<Edge> stateChangeStatements;

  public TransitionFunctionImpl(
      @Nonnull Set<? extends ITransition> trans, @Nonnull Set<Edge> stateChangeStatements) {
    this.stateChangeStatements = stateChangeStatements;
    this.values = trans;
  }

  public TransitionFunctionImpl(
      @Nonnull ITransition trans, @Nonnull Set<Edge> stateChangeStatements) {
    this(Collections.singleton(trans), stateChangeStatements);
  }

  @Override
  @Nonnull
  public Collection<ITransition> getValues() {
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
    final Weight one = TransitionFunctionRepresentativeOne.getInstanceOne();
    if (other == one) {
      return this;
    }
    final Weight zero = TransitionFunctionRepresentativeZero.getInstanceZero();
    if (other == zero) {
      return zero;
    }
    TransitionFunctionImpl func = (TransitionFunctionImpl) other;
    Set<? extends ITransition> otherTransitions = func.values;
    Set<ITransition> ress = new HashSet<>();
    Set<Edge> newStateChangeStatements = new HashSet<>();
    for (ITransition first : values) {
      for (ITransition second : otherTransitions) {
        if (second.equals(Transition.identity())) {
          ress.add(first);
          newStateChangeStatements.addAll(stateChangeStatements);
        } else if (first.equals(Transition.identity())) {
          ress.add(second);
          newStateChangeStatements.addAll(func.stateChangeStatements);
        } else if (first.to().equals(second.from())) {
          ress.add(new Transition(first.from(), second.to()));
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
    if (other == TransitionFunctionRepresentativeZero.getInstanceZero()) {
      return this;
    }
    Weight one = TransitionFunctionRepresentativeOne.getInstanceOne();
    TransitionFunction func = (TransitionFunction) other;
    Set<ITransition> transitions = new HashSet<>(values);
    HashSet<Edge> newStateChangeStmts = Sets.newHashSet(stateChangeStatements);
    if (other == one) {
      Set<ITransition> idTransitions = Sets.newHashSet();
      for (ITransition t : transitions) {
        idTransitions.add(new Transition(t.from(), t.from()));
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
