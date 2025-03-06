package inference;
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

import wpds.impl.Weight;

import javax.annotation.Nonnull;

public class InferenceWeightOne implements Weight {

  @Nonnull private static final InferenceWeightOne one  = new InferenceWeightOne();;

  private InferenceWeightOne() {}

  @Nonnull
  @Override
  public Weight extendWith(@Nonnull Weight other) {
    if (other.equals(one())) {
      return this;
    }
    if (this.equals(one())) {
      return other;
    }
    if (other.equals(zero()) || this.equals(zero())) {
      return zero();
    }
    throw new IllegalStateException("This should not happen!");
  }

  @Nonnull
  @Override
  public Weight combineWith(@Nonnull Weight other) {
    return extendWith(other);
  }

  @Nonnull public static InferenceWeightOne one() {
    return one;
  }

  public String toString() {
    return "ONE";
  }
}
