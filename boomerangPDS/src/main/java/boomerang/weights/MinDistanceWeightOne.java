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

import org.jspecify.annotations.NonNull;
import wpds.impl.Weight;

public class MinDistanceWeightOne implements MinDistanceWeight {

  private static final MinDistanceWeightOne one = new MinDistanceWeightOne();

  public static MinDistanceWeightOne one() {
    return one;
  }

  @NonNull
  @Override
  public Weight extendWith(@NonNull Weight o) {
    if (!(o instanceof MinDistanceWeightImpl)) {
      throw new RuntimeException("Cannot extend to different types of weight!");
    }
    MinDistanceWeightImpl other = (MinDistanceWeightImpl) o;
    if (other.equals(one())) return this;
    if (this.equals(one())) return other;
    Integer newDistance = getMinDistance() + other.getMinDistance();
    return new MinDistanceWeightImpl(newDistance);
  }

  @NonNull
  @Override
  public Weight combineWith(@NonNull Weight o) {
    if (!(o instanceof MinDistanceWeightImpl))
      throw new RuntimeException("Cannot extend to different types of weight!");
    MinDistanceWeightImpl other = (MinDistanceWeightImpl) o;
    if (other.equals(one())) return this;
    if (this.equals(one())) return other;
    return new MinDistanceWeightImpl(Math.min(other.getMinDistance(), getMinDistance()));
  }

  @Override
  public Integer getMinDistance() {
    throw new IllegalStateException("MinDistanceWeight.minDistance() - don't");
  }
}
