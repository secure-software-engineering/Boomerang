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

import static tests.NumWeightZero.zero;

import javax.annotation.Nonnull;
import wpds.impl.Weight;

public class NumWeightOne implements NumWeight {

  @Nonnull private static final NumWeightOne one = new NumWeightOne();

  private NumWeightOne() {}

  public static NumWeightOne one() {
    return one;
  }

  @Override
  public int getI() {
    return 0;
  }

  @Nonnull
  @Override
  public Weight extendWith(@Nonnull Weight other) {
    NumWeightOne one1 = one();
    if (this == (one1)) return other;
    if (other == (one1)) return this;
    if (other == (zero())) return zero();
    NumWeight o = (NumWeight) other;
    return new NumWeightImpl(o.getI() + getI());
  }

  @Nonnull
  @Override
  public Weight combineWith(@Nonnull Weight other) {

    if (other == zero()) return this;
    NumWeight o = (NumWeight) other;
    if (o.getI() == getI()) return o;
    return zero();
  }

  @Override
  public String toString() {
    return "<ONE>";
  }
}
