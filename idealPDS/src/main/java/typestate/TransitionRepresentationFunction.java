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

import java.util.Collection;
import javax.annotation.Nonnull;
import typestate.finiteautomata.ITransition;
import wpds.impl.Weight;

public class TransitionRepresentationFunction implements TransitionFunction {

  private static TransitionRepresentationFunction one;
  private static TransitionRepresentationFunction zero;

  public TransitionRepresentationFunction() {}

  @Nonnull
  @Override
  public Collection<ITransition> values() {
    throw new IllegalStateException("don't");
  }

  @Nonnull
  @Override
  public Weight extendWith(@Nonnull Weight other) {
    if (other.equals(one())) {
      return this;
    }
    if (this.equals(one())) return other;
    if (other.equals(zero()) || this.equals(zero())) {
      return zero();
    }
    throw new IllegalStateException("This should not happen!");
  }

  @Nonnull
  @Override
  public Weight combineWith(@Nonnull Weight other) {
    if (!(other instanceof TransitionRepresentationFunction)) {
      throw new RuntimeException();
    }
    if (this.equals(zero())) {
      return other;
    }
    if (other.equals(zero())) return this;
    if (other.equals(one()) && this.equals(one())) {
      return one();
    }
    return extendWith(other);
  }

  public static <W extends Weight> W one() {
    if (one == null) {
      one = new TransitionRepresentationFunction();
    }
    return (W) one;
  }

  public static <W extends Weight> W zero() {
    if (zero == null) {
      zero = new TransitionRepresentationFunction();
    }
    return (W) zero;
  }

  public String toString() {
    return (this == one) ? "ONE" : "ZERO";
  }
}
