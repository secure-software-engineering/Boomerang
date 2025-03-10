package boomerang.weights;

import wpds.impl.Weight;

import static boomerang.weights.MinDistanceWeightOne.one;


public class MinDistanceWeight implements MinDistanceWeightInterface {

  private Integer minDistance = -1;
  private static MinDistanceWeight one;
  private static MinDistanceWeight zero;



  public MinDistanceWeight(Integer minDistance) {
    this.minDistance = minDistance;
  }

  public MinDistanceWeight() {

  }


  @Override
  public Weight extendWith( Weight o) {
    if (!(o instanceof MinDistanceWeight))
      throw new RuntimeException("Cannot extend to different types of weight!");
    MinDistanceWeight other = (MinDistanceWeight) o;
    if (other.equals(one())) return this;
    if (this.equals(one())) return other;
    Integer newDistance = minDistance + other.minDistance;
    return new MinDistanceWeight(newDistance);
  }


  @Override
  public Weight combineWith(Weight o) {
    if (!(o instanceof MinDistanceWeight))
      throw new RuntimeException("Cannot extend to different types of weight!");
    MinDistanceWeight other = (MinDistanceWeight) o;
    if (other.equals(one())) return this;
    if (this.equals(one())) return other;
    return new MinDistanceWeight(Math.min(other.minDistance, minDistance));
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
    MinDistanceWeight other = (MinDistanceWeight) obj;
    if (minDistance == null) {
      if (other.minDistance != null) return false;
    } else if (!minDistance.equals(other.minDistance)) return false;
   return false;
  }

  @Override
  public String toString() {
    final Weight one= new MinDistanceWeightOne();
    return (this==one) ? "ONE " : " Distance: " + minDistance;
  }

  @Override
  public Integer getMinDistance() {
    return minDistance;
  }
}
