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
import typestate.finiteautomata.ITransition;
import wpds.impl.Weight;

public class TransitionFunctionRepresentativeZero implements TransitionFunction {

  private static TransitionFunctionRepresentativeZero zero;

  public TransitionFunctionRepresentativeZero() {}

  @Nonnull
  @Override
  public Collection<ITransition> getValues() {
    throw new IllegalStateException("don't");
  }

  @Nonnull
  @Override
  public Set<ControlFlowGraph.Edge> getStateChangeStatements() {
    throw new IllegalStateException("This should not happen!");
  }

  @Nonnull
  @Override
  public Weight extendWith(@Nonnull Weight other) {
    TransitionFunctionRepresentativeOne one = TransitionFunctionRepresentativeOne.one();
    if (other == one) {
      return this;
    }
    TransitionFunctionRepresentativeZero zero = zero();
    if (other == zero) {
      return zero;
    }
    throw new IllegalStateException("This should not happen!");
  }

  @Nonnull
  @Override
  public Weight combineWith(@Nonnull Weight other) {
    if (!(other instanceof TransitionFunctionRepresentativeZero)) {
      throw new RuntimeException();
    }
    return other;
  }

  @Nonnull
  public static TransitionFunctionRepresentativeZero zero() {
    if (zero == null) {
      zero = new TransitionFunctionRepresentativeZero();
    }
    return zero;
  }

  public String toString() {
    return "ZERO";
  }
}
