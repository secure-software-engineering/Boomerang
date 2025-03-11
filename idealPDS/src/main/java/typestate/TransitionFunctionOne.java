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
import javax.annotation.Nonnull;
import typestate.finiteautomata.Transition;
import wpds.impl.Weight;

public class TransitionFunctionOne implements TransitionFunction {

  @Nonnull private static final TransitionFunctionOne one = new TransitionFunctionOne();

  public TransitionFunctionOne() {}

  public static TransitionFunctionOne one() {
    return one;
  }

  @Nonnull
  @Override
  public Collection<Transition> getValues() {
    throw new IllegalStateException("TransitionFunctionOne.getValues() - don't");
  }

  @Nonnull
  @Override
  public Set<ControlFlowGraph.Edge> getStateChangeStatements() {
    throw new IllegalStateException(
        "TransitionFunctionOne.getStateChangeStatements() - This should not happen!");
  }

  @Nonnull
  @Override
  public Weight extendWith(@Nonnull Weight other) {
    return other;
  }

  @Nonnull
  @Override
  public Weight combineWith(@Nonnull Weight other) {
    if (!(other instanceof TransitionFunctionOne)) {
      throw new RuntimeException("TransitionFunctionOne.combineWith() - don't");
    }
    return other;
  }

  public String toString() {
    return "ONE";
  }
}
