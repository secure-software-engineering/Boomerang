package sync.pds.weights;

import java.util.Collection;
import javax.annotation.Nonnull;
import sync.pds.solver.nodes.Node;
import wpds.impl.Weight;

public interface SetDomain<N, Stmt, Fact> extends Weight {
  @Nonnull
  Weight extendWith(@Nonnull Weight other);

  @Nonnull
  Weight combineWith(@Nonnull Weight other);

  @Nonnull
  Collection<Node<Stmt, Fact>> getNodes();

  @Nonnull
  Collection<Node<Stmt, Fact>> elements();
}
