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
package tests;

import org.jspecify.annotations.NonNull;
import wpds.impl.Weight;

public class NumWeightZero implements NumWeight {

  @NonNull private static final NumWeightZero zero = new NumWeightZero();

  private NumWeightZero() {}

  @NonNull
  public static NumWeightZero zero() {
    return zero;
  }

  @Override
  public int getI() {
    return 0;
  }

  @NonNull
  @Override
  public Weight extendWith(@NonNull Weight other) {
    return zero();
  }

  @NonNull
  @Override
  public Weight combineWith(@NonNull Weight other) {
    return other;
  }

  @Override
  public String toString() {
    return "<ZERO>";
  }
}
