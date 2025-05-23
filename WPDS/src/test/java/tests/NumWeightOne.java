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

import static tests.NumWeightZero.zero;

import org.jspecify.annotations.NonNull;
import wpds.impl.Weight;

public class NumWeightOne implements NumWeight {

  @NonNull private static final NumWeightOne one = new NumWeightOne();

  private NumWeightOne() {}

  public static NumWeightOne one() {
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
    NumWeightZero zero = zero();
    if (other == zero) {
      return this;
    }
    NumWeight o = (NumWeight) other;
    if (o.getI() == getI()) {
      return o;
    }
    return zero;
  }

  @Override
  public String toString() {
    return "<ONE>";
  }
}
