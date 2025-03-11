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

public class InferenceWeightOne implements InferenceWeight {

  @Nonnull private static final InferenceWeightOne one = new InferenceWeightOne();

  private InferenceWeightOne() {}

  @Nonnull
  @Override
  public Weight extendWith(@Nonnull Weight other) {
    InferenceWeightOne one = one();
    if (other == one) {
      return this;
    }
    return other;
  }

  @Nonnull
  @Override
  public Weight combineWith(@Nonnull Weight other) {
    return extendWith(other);
  }

  @Nonnull
  public static InferenceWeightOne one() {
    return one;
  }

  public String toString() {
    return "ONE";
  }
}
