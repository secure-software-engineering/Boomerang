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
package boomerang.weights;

import org.jspecify.annotations.NonNull;
import wpds.impl.Weight;

public class MinDistanceWeightOne implements MinDistanceWeight {

  private static final MinDistanceWeightOne one = new MinDistanceWeightOne();

  private MinDistanceWeightOne() {
    /* Singleton */
  }

  public static MinDistanceWeightOne one() {
    return one;
  }

  @Override
  public int getMinDistance() {
    throw new IllegalStateException("MinDistanceWeight.minDistance() - don't");
  }

  @NonNull
  @Override
  public Weight extendWith(@NonNull Weight o) {
    if (!(o instanceof MinDistanceWeight)) {
      throw new RuntimeException("Cannot extend to different types of weight!");
    }
    return o;
  }

  @NonNull
  @Override
  public Weight combineWith(@NonNull Weight o) {
    if (!(o instanceof MinDistanceWeight)) {
      throw new IllegalStateException("Cannot extend to different types of weight!");
    }
    return o;
  }
}
