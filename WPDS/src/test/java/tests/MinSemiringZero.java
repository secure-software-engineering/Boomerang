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

import static tests.MinSemiringOne.one;

import de.fraunhofer.iem.Location;
import org.jspecify.annotations.NonNull;
import wpds.impl.Weight;

public class MinSemiringZero implements MinSemiring {

  @NonNull private static final MinSemiringZero zero = new MinSemiringZero();

  private MinSemiringZero() {}

  public MinSemiringZero(int i) {}

  public static <N extends Location> MinSemiring zero() {
    return zero;
  }

  @Override
  public int getI() {
    return 0;
  }

  @NonNull
  @Override
  public Weight extendWith(@NonNull Weight other) {

    return new MinSemiringZero(((MinSemiringZero) other).getI() + getI());
  }

  @NonNull
  @Override
  public Weight combineWith(@NonNull Weight other) {
    return this;
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
    MinSemiringZero other = (MinSemiringZero) obj;
    return getI() == other.getI();
  }


}
