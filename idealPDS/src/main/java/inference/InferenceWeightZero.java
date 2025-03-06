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
import javax.annotation.Nonnull;
import wpds.impl.Weight;

public class InferenceWeightZero implements Weight {

  @Nonnull private static final InferenceWeightZero zero = new InferenceWeightZero();;

  private InferenceWeightZero() {}

  @Nonnull
  @Override
  public Weight extendWith(@Nonnull Weight other) {
      return zero();
  }

  @Nonnull
  @Override
  public Weight combineWith(@Nonnull Weight other) {
    return extendWith(other);
  }

  @Nonnull
  public static InferenceWeightZero zero() {
    return zero;
  }

  public String toString() {
    return "ZERO";
  }
}
