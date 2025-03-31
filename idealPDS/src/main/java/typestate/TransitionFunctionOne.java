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

import boomerang.scope.ControlFlowGraph;
import java.util.Collection;
import java.util.Set;
import org.jspecify.annotations.NonNull;
import typestate.finiteautomata.Transition;
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
    return other;
  }

  @NonNull
  @Override
  public Weight combineWith(@NonNull Weight other) {
    if (!(other instanceof TransitionFunction)) {
      throw new RuntimeException();
    }
    return this;
  }

  public String toString() {
    return "ONE";
  }
}
