/**
 * ***************************************************************************** 
 * Copyright (c) 2018 Fraunhofer IEM, Paderborn, Germany
 * <p>
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * <p>
 * SPDX-License-Identifier: EPL-2.0
 * <p>
 * Contributors:
 *   Johannes Spaeth - initial API and implementation
 * *****************************************************************************
 */
package tests;

import static tests.NumWeightOne.one;

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
    if (this.equals(one())) return other;
    if (other.equals(one())) return this;
    if (this.equals(zero()) || other.equals(zero())) return zero();
    NumWeight o = (NumWeight) other;
    return new NumWeightImpl(o.getI() + getI());
  }

  @NonNull
  @Override
  public Weight combineWith(@NonNull Weight other) {
    if (other.equals(zero())) return this;
    if (this.equals(zero())) return other;
    NumWeight o = (NumWeight) other;
    if (o.getI() == getI()) return o;
    return zero();
  }

  @Override
  public String toString() {
    return "<ZERO>";
  }
}
