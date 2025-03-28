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

public class MinSemiringImpl implements Weight, MinSemiring {
  int i;

  public MinSemiringImpl(int i) {
    this.i = i;
  }

  @NonNull
  @Override
  public Weight extendWith(@NonNull Weight other) {
    MinSemiring one = MinSemiringOne.one();
    if (other== one) return this;
    if (this== one) return other;
    MinSemiringImpl o = (MinSemiringImpl) other;
    return new MinSemiringImpl(o.i + i);
  }

  @NonNull
  @Override
  public Weight combineWith(@NonNull Weight other) {
    if (other== (MinSemiringZero.zero())) return this;
    if (this== (MinSemiringZero.zero())) return other;
    MinSemiringImpl o = (MinSemiringImpl) other;
    return new MinSemiringImpl(Math.min(o.i, i));
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
    MinSemiringImpl other = (MinSemiringImpl) obj;
    return i == other.i;
  }

  @Override
  public int getI() {
    return 0;
  }
}
