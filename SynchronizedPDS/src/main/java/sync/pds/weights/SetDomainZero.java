package sync.pds.weights;

import java.util.Collection;
import javax.annotation.Nonnull;
import sync.pds.solver.nodes.Node;
import wpds.impl.Weight;

public class SetDomainZero implements SetDomain {

  @Nonnull private static final SetDomainZero zero = new SetDomainZero();

  @Nonnull
  @Override
  public Weight extendWith(@Nonnull Weight other) {
    return zero();
  }

  @Nonnull
  @Override
  public Weight combineWith(@Nonnull Weight other) {
    if (!(other instanceof SetDomainZero)) {
      throw new RuntimeException("SetDomainZero.combineWith() - don't");
    }
    return other;
  }

  @Nonnull
  @Override
  public Collection<Node> getNodes() {
    throw new IllegalStateException("SetDomain.getNodes() - don't");
  }

  public static SetDomainZero zero() {
    return zero;
  }

  @Nonnull
  @Override
  public Collection<Node> elements() {
    throw new IllegalStateException("SetDomainZero.elements() - don't");
  }

  public String toString() {
    return "ZERO";
  }
}
