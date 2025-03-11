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

import de.fraunhofer.iem.Location;
import wpds.impl.Weight;

import javax.annotation.Nonnull;

public class NumWeightOne implements NumWeightInterface {

  @Nonnull private static final NumWeightOne one = new NumWeightOne();

  public NumWeightOne() {}

  public static NumWeightOne one() {
    return one;
  }



  @Nonnull
  @Override
  public Weight extendWith(@Nonnull Weight other) {
    return other;
  }

  @Nonnull
  @Override
  public Weight combineWith(@Nonnull Weight other) {
    if (!(other instanceof NumWeightOne)) {
      throw new RuntimeException("NumWeightOne.combineWith() - don't");
    }
    return other;
  }



  @Override
  public String toString() {
    return "ONE";
  }


}
