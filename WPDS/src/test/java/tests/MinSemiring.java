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

import de.fraunhofer.iem.Location;
import org.jspecify.annotations.NonNull;
import wpds.impl.Weight;

public class MinSemiring implements Weight {
  int i;

  public MinSemiring(int i) {
    this.i = i;
  }

  private MinSemiring() {}

  @NonNull
  @Override
  public Weight extendWith(@NonNull Weight other) {
    if (other.equals(one())) return this;
    if (this.equals(one())) return other;
    MinSemiring o = (MinSemiring) other;
    return new MinSemiring(o.i + i);
  }

  @NonNull
  @Override
  public Weight combineWith(@NonNull Weight other) {
    if (other.equals(zero())) return this;
    if (this.equals(zero())) return other;
    MinSemiring o = (MinSemiring) other;
    return new MinSemiring(Math.min(o.i, i));
  }

  private static MinSemiring one;

  public static <N extends Location> MinSemiring one() {
    if (one == null)
      one =
          new MinSemiring(0) {
            @Override
            public String toString() {
              return "<ONE>";
            }
          };
    return one;
  }

  private static MinSemiring zero;

  public static <N extends Location> MinSemiring zero() {
    if (zero == null)
      zero =
          new MinSemiring(110000) {
            @Override
            public String toString() {
              return "<ZERO>";
            }
          };
    return zero;
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
    MinSemiring other = (MinSemiring) obj;
    return i == other.i;
  }
}
