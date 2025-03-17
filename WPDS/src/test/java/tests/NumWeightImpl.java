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
import static tests.NumWeightZero.zero;

import javax.annotation.Nonnull;
import wpds.impl.Weight;

public class NumWeightImpl implements NumWeight {

  private int i;

  public NumWeightImpl(int i) {
    this.i = i;
  }

  public int getI() {
    return i;
  }

  @Nonnull
  @Override
  public Weight extendWith(@Nonnull Weight other) {
    if (this.equals(one())) return other;
    if (other.equals(one())) return this;
    if (this.equals(zero()) || other.equals(zero())) return zero();
    NumWeightImpl o = (NumWeightImpl) other;
    return new NumWeightImpl(o.i + i);
  }

  @Nonnull
  @Override
  public Weight combineWith(@Nonnull Weight other) {
    if (other.equals(zero())) return this;
    if (this.equals(zero())) return other;
    NumWeightImpl o = (NumWeightImpl) other;
    if (o.i == i) return o;
    return zero();
  }

  @Override
  public String toString() {
    return Integer.toString(i);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + i;
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    NumWeightImpl other = (NumWeightImpl) obj;
    return i == other.i;
  }
}
