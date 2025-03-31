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

public class MinDistanceWeightImpl implements MinDistanceWeight {

  private final Integer minDistance;

  public MinDistanceWeightImpl(Integer minDistance) {
    this.minDistance = minDistance;
  }

  @NonNull
  @Override
  public Weight extendWith(@NonNull Weight o) {
    if (!(o instanceof MinDistanceWeight)) {
      throw new RuntimeException("Cannot extend to different types of weight!");
    }
    MinDistanceWeightImpl other = (MinDistanceWeightImpl) o;

    Integer newDistance = minDistance + other.minDistance;
    return new MinDistanceWeightImpl(newDistance);
  }

  @NonNull
  @Override
  public Weight combineWith(@NonNull Weight o) {
    if (!(o instanceof MinDistanceWeight))
      throw new RuntimeException("Cannot extend to different types of weight!");
    return new MinDistanceWeightImpl(Math.min(((MinDistanceWeightImpl) o).minDistance, minDistance));
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((minDistance == null) ? 0 : minDistance.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    MinDistanceWeightImpl other = (MinDistanceWeightImpl) obj;
    if (minDistance == null) {
      if (other.minDistance != null) return false;
    } else if (!minDistance.equals(other.minDistance)) return false;
    return false;
  }

  @Override
  public String toString() {

    return " Distance: " + minDistance;
  }

  @Override
  public Integer getMinDistance() {
    return minDistance;
  }
}
