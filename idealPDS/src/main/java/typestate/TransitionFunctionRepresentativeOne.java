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

public class TransitionFunctionRepresentativeOne implements TransitionFunction {

  private static final TransitionFunctionRepresentativeOne one =
      new TransitionFunctionRepresentativeOne();

  public TransitionFunctionRepresentativeOne() {}

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
    if (other == one()) {
      return this;
    }
    return other;
  }

  @Nonnull
  @Override
  public Weight combineWith(@Nonnull Weight other) {
    if (!(other instanceof TransitionFunctionRepresentativeOne)) {
      throw new RuntimeException("don't");
    }
    TransitionFunctionRepresentativeOne one = one();
    if (other == one) {
      return one;
    }
    return extendWith(other);
  }

  public static TransitionFunctionRepresentativeOne one() {
    return one;
  }

  public String toString() {
    return "ONE";
  }
}
