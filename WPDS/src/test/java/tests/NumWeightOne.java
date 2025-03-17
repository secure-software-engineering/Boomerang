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

import static tests.NumWeightZero.zero;

public class NumWeightOne implements NumWeight {

  @Nonnull private static final NumWeightOne one = new NumWeightOne();
  private int i;

  public NumWeightOne() {}

  public static NumWeightOne one() {
    return one;
  }

  @Nonnull
  @Override
  public Weight extendWith(@Nonnull Weight other) {
    if (this.equals(one())) return other;
    if (other.equals(one())) return this;
    if (this.equals(zero()) || other.equals(zero())) return zero();


     NumWeightOne o = (NumWeightOne) other;
    return new NumWeightImpl(o.i + i);
    
  }

  @Nonnull
  @Override
  public Weight combineWith(@Nonnull Weight other) {
    if (other.equals(zero())) return this;
    if (this.equals(zero())) return other;


    NumWeightOne o = (NumWeightOne) other;
    if (o.i == i) return o;
    return zero();


  }

  @Override
  public String toString() {
    return "<ONE>";
  }


}
