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
  public Multimap<Transition, StatementSequence> getStateChangeSequences() {
    return HashMultimap.create();
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
      throw new IllegalStateException(
          "Cannot combine TransitionFunction with non TransitionsFunction");
    }

    if (other == zero() || other == one()) {
      return this;
    }

    TransitionFunctionImpl func = (TransitionFunctionImpl) other;
    Multimap<Transition, StatementSequence> result =
        HashMultimap.create(func.getStateChangeSequences());
    for (Transition t : func.getStateChangeSequences().keySet()) {
      Transition transition = new TransitionImpl(t.from(), t.from());
      Collection<StatementSequence> statement = func.getStateChangeSequences().get(t);

      result.putAll(transition, statement);
    }

    return new TransitionFunctionImpl(
        func.getStateChangeSequences(), func.getStateChangeStatement());
  }

  public String toString() {
    return "ONE";
  }
}
