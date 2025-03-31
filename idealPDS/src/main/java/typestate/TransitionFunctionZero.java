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

public class TransitionFunctionZero implements TransitionFunction {

  @NonNull private static final TransitionFunctionZero zero = new TransitionFunctionZero();

  public TransitionFunctionZero() {}

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
    return zero();
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
    return "ZERO";
  }
}
