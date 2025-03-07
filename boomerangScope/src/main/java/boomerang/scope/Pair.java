package boomerang.scope;

import java.util.Objects;

public class Pair<X, Y> {

  private final Y y;
  private final X x;

  public Pair(X x, Y y) {
    this.x = x;
    this.y = y;
  }

  public X getX() {
    return x;
  }

  public Y getY() {
    return y;
  }

  @Override
  public String toString() {
    return "Pair(" + x + "," + y + ")";
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Pair<?, ?> pair = (Pair<?, ?>) o;
    return Objects.equals(y, pair.y) && Objects.equals(x, pair.x);
  }

  @Override
  public int hashCode() {
    return Objects.hash(y, x);
  }
}
