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

import static tests.MinSemiringZero.zero;

import de.fraunhofer.iem.Location;
import org.jspecify.annotations.NonNull;
import wpds.impl.Weight;

public class MinSemiringOne implements MinSemiring {

  @NonNull private static final MinSemiringOne one = new MinSemiringOne();

  private MinSemiringOne() {}

  public MinSemiringOne(int i) {}

  public static <N extends Location> MinSemiring one() {
    return one;
  }

  @Override
  public int getI() {
    return 0;
  }

  @NonNull
  @Override
  public Weight extendWith(@NonNull Weight other) {
    return other;
  }

  @NonNull
  @Override
  public Weight combineWith(@NonNull Weight other) {
    if (other == zero()) return this;
    MinSemiring o = (MinSemiring) other;
    return new MinSemiringOne(Math.min(o.getI(), getI()));
  }

  @Override
  public String toString() {
    return Integer.toString(getI());
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + getI();
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    MinSemiringOne other = (MinSemiringOne) obj;
    return getI() == other.getI();
  }


}
