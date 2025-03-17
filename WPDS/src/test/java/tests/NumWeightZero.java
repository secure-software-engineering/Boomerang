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

import static tests.NumWeightOne.one;

import javax.annotation.Nonnull;
import wpds.impl.Weight;

public class NumWeightZero implements NumWeight {

  @Nonnull private static final NumWeightZero zero = new NumWeightZero();

  private NumWeightZero() {}

  @Nonnull
  public static NumWeightZero zero() {
    return zero;
  }

  @Override
  public int getWeight() {
    return 0;
  }

  @Nonnull
  @Override
  public Weight extendWith(@Nonnull Weight other) {
    if (this.equals(one())) return other;
    if (other.equals(one())) return this;
    if (this.equals(zero()) || other.equals(zero())) return zero();
    NumWeightImpl o = (NumWeightImpl) other;
    return new NumWeightImpl(o.getWeight() + getWeight());
  }

  @Nonnull
  @Override
  public Weight combineWith(@Nonnull Weight other) {
    if (other.equals(zero())) return this;
    if (this.equals(zero())) return other;
    NumWeightImpl o = (NumWeightImpl) other;
    if (o.getWeight() == getWeight()) return o;
    return zero();
  }

  @Override
  public String toString() {
    return "<ZERO>";
  }
}
