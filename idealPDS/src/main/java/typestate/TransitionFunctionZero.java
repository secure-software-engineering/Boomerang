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

import boomerang.scope.ControlFlowGraph;
import java.util.Collection;
import java.util.Set;
import javax.annotation.Nonnull;
import typestate.finiteautomata.Transition;
import wpds.impl.Weight;

public class TransitionFunctionZero implements TransitionFunction {

  @Nonnull private static final TransitionFunctionZero zero = new TransitionFunctionZero();

  public TransitionFunctionZero() {}

  @Nonnull
  public static TransitionFunctionZero zero() {
    return zero;
  }

  @Nonnull
  @Override
  public Collection<Transition> getValues() {
    throw new IllegalStateException("TransitionZero.getValues() - don't");
  }

  @Nonnull
  @Override
  public Set<ControlFlowGraph.Edge> getStateChangeStatements() {
    throw new IllegalStateException("This should not happen!");
  }

  @Nonnull
  @Override
  public Weight extendWith(@Nonnull Weight other) {
    return this;
  }

  @Nonnull
  @Override
  public Weight combineWith(@Nonnull Weight other) {
    if (!(other instanceof TransitionFunctionZero)) {
      throw new RuntimeException("TransitionFunctionZero.combineWith() - don't");
    }
    return other;
  }

  public String toString() {
    return "ZERO";
  }
}
