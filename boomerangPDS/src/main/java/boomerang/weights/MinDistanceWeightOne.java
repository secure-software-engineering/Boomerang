package boomerang.weights;

import wpds.impl.Weight;

import javax.annotation.Nonnull;

public class MinDistanceWeightOne implements MinDistanceWeight {

  private static final MinDistanceWeightOne one = new MinDistanceWeightOne();

  public static MinDistanceWeightOne one() {
    return one;
  }

  @Nonnull
  @Override
  public Weight extendWith(@Nonnull Weight o) {
    throw new IllegalStateException("MinDistanceWeight.extendWith() - don't");

  }

  @Nonnull
  @Override
  public Weight combineWith(@Nonnull Weight o) {
    throw new IllegalStateException("MinDistanceWeight.combineWith() - don't");
  }

  @Override
  public Integer getMinDistance() {
    throw new IllegalStateException("MinDistanceWeight.minDistance() - don't");
  }
}
