package boomerang.weights;

import wpds.impl.Weight;

public class MinDistanceWeightOne implements MinDistanceWeight {

  private static final MinDistanceWeightOne one = new MinDistanceWeightOne();

  public static MinDistanceWeightOne one() {
    return one;
  }

  @Override
  public Weight extendWith(Weight o) {
    return null;
  }

  @Override
  public Weight combineWith(Weight o) {
    return null;
  }

  @Override
  public Integer getMinDistance() {
    return 0;
  }
}
