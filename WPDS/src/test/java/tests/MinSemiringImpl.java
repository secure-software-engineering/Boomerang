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

import static tests.MinSemiringOne.one;
import static tests.MinSemiringZero.zero;

import org.jspecify.annotations.NonNull;
import wpds.impl.Weight;

public class MinSemiringImpl implements MinSemiring {

  protected final int value;

  public MinSemiringImpl(int value) {
    this.value = value;
  }

  public int getValue() {
    return value;
  }

  @NonNull
  @Override
  public Weight extendWith(@NonNull Weight other) {
    if (other == one()) {
      return this;
    }
    MinSemiringImpl o = (MinSemiringImpl) other;
    return new MinSemiringImpl(o.value + value);
  }

  @NonNull
  @Override
  public Weight combineWith(@NonNull Weight other) {
    if (other == zero()) {
      return this;
    }
    MinSemiringImpl o = (MinSemiringImpl) other;
    return new MinSemiringImpl(Math.min(o.value, value));
  }

  @Override
  public String toString() {
    return Integer.toString(value);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + value;
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    MinSemiringImpl other = (MinSemiringImpl) obj;
    return value == other.value;
  }
}
