package boomerang.weights;

import wpds.impl.Weight;

public interface MinDistanceWeight extends Weight {

  @Override
  Weight extendWith(Weight o);

  @Override
  Weight combineWith(Weight o);

  Integer getMinDistance();
}
