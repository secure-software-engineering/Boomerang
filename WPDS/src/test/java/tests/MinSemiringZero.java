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

import de.fraunhofer.iem.Location;
import org.jspecify.annotations.NonNull;
import wpds.impl.Weight;

class MinSemiringZero extends MinSemiringImpl {
  private static final MinSemiringZero zero = new MinSemiringZero();

  private MinSemiringZero() {
    /* Singleton*/
    super(110000);
  }

  public static <N extends Location> MinSemiringZero zero() {
    return zero;
  }

  @NonNull
  @Override
  public Weight extendWith(@NonNull Weight other) {
    if (other == one()) {
      return this;
    }
    MinSemiringImpl o = (MinSemiringImpl) other;
    return new MinSemiringImpl(o.getValue() + getValue());
  }

  @NonNull
  @Override
  public Weight combineWith(@NonNull Weight other) {
    return other;
  }

  @Override
  public String toString() {
    return "<ZERO>";
  }
}
