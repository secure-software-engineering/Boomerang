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

  private final Set<ITransition> value;
  private final Set<Edge> stateChangeStatements;

  public TransitionFunctionImpl(Set<? extends ITransition> trans, Set<Edge> stateChangeStatements) {
    this.stateChangeStatements = stateChangeStatements;
    this.value = new HashSet<>(trans);
  }

  public TransitionFunctionImpl(ITransition trans, Set<Edge> stateChangeStatements) {
    this(new HashSet<>(Collections.singleton(trans)), stateChangeStatements);
  }

  @Nonnull
  public Collection<ITransition> values() {
    return Lists.newArrayList(value);
  }

  @Nonnull
  @Override
  public Weight extendWith(@Nonnull Weight other) {
    final Weight one = TransitionRepresentationFunction.one();
    if (other == one) {
      return this;
    }
    if (this == one) {
      return other;
    }
    final Weight zero = TransitionRepresentationFunction.zero();
    if (other == zero || this == zero) {
      return zero;
    }
    TransitionFunctionImpl func = (TransitionFunctionImpl) other;
    Set<ITransition> otherTransitions = func.value;
    Set<ITransition> ress = new HashSet<>();
    Set<Edge> newStateChangeStatements = new HashSet<>();
    for (ITransition first : value) {
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
    if (!(other instanceof TransitionFunctionImpl)) {
      throw new RuntimeException();
    }
    if (this.equals(TransitionRepresentationFunction.zero())) {
      return other;
    }
    if (other.equals(TransitionRepresentationFunction.zero())) {
        return this;
    }
    Weight one = TransitionRepresentationFunction.one();
    if (other == one && this == one) {
      return one;
    }
    TransitionFunctionImpl func = (TransitionFunctionImpl) other;
    if (other == one || this == one) {
      Set<ITransition> transitions = new HashSet<>((other == one ? value : func.value));
      Set<ITransition> idTransitions = Sets.newHashSet();
      for (ITransition t : transitions) {
        idTransitions.add(new Transition(t.from(), t.from()));
      }
      transitions.addAll(idTransitions);
      return new TransitionFunctionImpl(
          transitions,
          Sets.newHashSet(
              (other == one) ? stateChangeStatements : func.stateChangeStatements));
    }
    Set<ITransition> transitions = new HashSet<>(func.value);
    transitions.addAll(value);
    HashSet<Edge> newStateChangeStmts = Sets.newHashSet(stateChangeStatements);
    newStateChangeStmts.addAll(func.stateChangeStatements);
    return new TransitionFunctionImpl(transitions, newStateChangeStmts);
  }

  public String toString() {
    return "Weight: " + value.toString();
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((value == null) ? 0 : value.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    TransitionFunctionImpl other = (TransitionFunctionImpl) obj;
    if (value == null) {
      return other.value == null;
    } else return value.equals(other.value);
  }
}
