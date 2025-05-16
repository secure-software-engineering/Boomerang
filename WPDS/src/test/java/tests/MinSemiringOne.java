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

import static tests.MinSemiringZero.zero;

import de.fraunhofer.iem.Location;
import org.jspecify.annotations.NonNull;
import wpds.impl.Weight;

class MinSemiringOne extends MinSemiringImpl {

  private static final MinSemiringOne one = new MinSemiringOne();

  private MinSemiringOne() {
    super(0);
    /* Singleton */
  }

  public static <N extends Location> MinSemiringOne one() {
    return one;
  }

  @NonNull
  @Override
  public Weight extendWith(@NonNull Weight other) {
    return other;
  }

  @NonNull
  @Override
  public Weight combineWith(@NonNull Weight other) {
    if (other == zero()) {
      return this;
    }
    MinSemiring o = (MinSemiring) other;
    return new MinSemiringImpl(Math.min(o.getValue(), value));
  }

  @Override
  public String toString() {
    return "<ONE>";
  }
}
