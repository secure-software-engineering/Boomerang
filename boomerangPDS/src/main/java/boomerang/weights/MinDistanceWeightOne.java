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
package boomerang.weights;

import javax.annotation.Nonnull;
import wpds.impl.Weight;

public class MinDistanceWeightOne implements MinDistanceWeight {

  private static final MinDistanceWeightOne one = new MinDistanceWeightOne();

  public static MinDistanceWeightOne one() {
    return one;
  }

  @Nonnull
  @Override
  public Weight extendWith(@Nonnull Weight o) {
    if (!(o instanceof MinDistanceWeightImpl)) {
      throw new RuntimeException("Cannot extend to different types of weight!");
    }
    MinDistanceWeightImpl other = (MinDistanceWeightImpl) o;
    MinDistanceWeight one1 = one();
    if (this== one1) return other;
    Integer newDistance = getMinDistance() + other.getMinDistance();
    return new MinDistanceWeightImpl(newDistance);
  }

  @Nonnull
  @Override
  public Weight combineWith(@Nonnull Weight o) {
    if (!(o instanceof MinDistanceWeightImpl))
      throw new RuntimeException("Cannot extend to different types of weight!");
    MinDistanceWeightImpl other = (MinDistanceWeightImpl) o;
    MinDistanceWeight one1 = one();
    if (this== one1) return other;
    return new MinDistanceWeightImpl(Math.min(other.getMinDistance(), getMinDistance()));
  }

  @Override
  public Integer getMinDistance() {
    throw new IllegalStateException("MinDistanceWeight.minDistance() - don't");
  }
}
