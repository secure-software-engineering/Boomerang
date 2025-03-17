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

import wpds.impl.Weight;

import javax.annotation.Nonnull;

import static tests.NumWeightOne.one;

public class NumWeightZero implements NumWeight {

  @Nonnull private static final NumWeightZero zero = new NumWeightZero();

  public NumWeightZero() {}

  @Nonnull
  public static NumWeightZero zero() {
    return zero;
  }
  public NumWeightZero(int i) {

  }



  @Nonnull
  @Override
  public Weight extendWith(@Nonnull Weight other) {
    if (this.equals(one())) return other;
    if (other.equals(one())) return this;
    if (this.equals(zero()) || other.equals(zero())) return zero();


     NumWeightZero o = (NumWeightZero) other;
    return new NumWeightZero(o.getI() + getI());

  }

  private int getI() {
  return 0;
  }

  @Nonnull
  @Override
  public Weight combineWith(@Nonnull Weight other) {
    if (other.equals(zero())) return this;
    if (this.equals(zero())) return other;
    NumWeightZero o = (NumWeightZero) other;
    return new NumWeightZero(o.getI() + getI());

  }




  @Override
  public String toString() {
    return "<ZERO>";
  }


}
