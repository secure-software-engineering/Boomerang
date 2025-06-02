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

import static typestate.TransitionFunctionZero.zero;

import boomerang.scope.Statement;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import java.util.Collection;
import org.jspecify.annotations.NonNull;
import typestate.finiteautomata.Transition;
import typestate.finiteautomata.TransitionImpl;
import wpds.impl.Weight;

public class TransitionFunctionOne implements TransitionFunction {

  @NonNull private static final TransitionFunctionOne one = new TransitionFunctionOne();

  private TransitionFunctionOne() {}

  public static TransitionFunctionOne one() {
    return one;
  }

  @NonNull
  @Override
  public Multimap<Transition, Statement> getStateChangeStatements() {
    throw new IllegalStateException("TransitionFunctionOne.getStateChangeStatements() - don't");
  }

  @NonNull
  @Override
  public Weight extendWith(@NonNull Weight other) {
    return other;
  }

  @NonNull
  @Override
  public Weight combineWith(@NonNull Weight other) {
    if (!(other instanceof TransitionFunction)) {
      throw new IllegalStateException("should not happen!");
    }

    if (other == zero() || other == one()) {
      return this;
    }

    /*TransitionFunction func = (TransitionFunction) other;
    Set<Transition> transitions = new HashSet<>(func.getValues());
    Set<Transition> idTransitions = Sets.newHashSetWithExpectedSize(transitions.size());
    for (Transition t : transitions) {
      idTransitions.add(new TransitionImpl(t.from(), t.from()));
    }
    transitions.addAll(idTransitions);
    return new TransitionFunctionImpl(
        transitions, Sets.newHashSet(func.getStateChangeStatementsOld()));*/
    TransitionFunction func = (TransitionFunction) other;
    Multimap<Transition, Statement> result = HashMultimap.create(func.getStateChangeStatements());
    for (Transition t : func.getStateChangeStatements().keySet()) {
      Transition transition = new TransitionImpl(t.from(), t.from());
      Collection<Statement> statement = func.getStateChangeStatements().get(t);

      result.putAll(transition, statement);
    }

    return new TransitionFunctionImpl(result);
  }

  public String toString() {
    return "ONE";
  }
}
