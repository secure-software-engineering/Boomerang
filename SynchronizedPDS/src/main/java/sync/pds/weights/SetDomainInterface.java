package sync.pds.weights;

import sync.pds.solver.nodes.Node;
import wpds.impl.Weight;

import javax.annotation.Nonnull;
import java.util.Collection;

public interface SetDomainInterface<N, Stmt, Fact> extends Weight {
    @Nonnull
    Weight extendWith(  @Nonnull Weight other);

    @Nonnull
    Weight combineWith(  @Nonnull Weight other);

    @Nonnull
    Collection<Node<Stmt, Fact>> getNodes();

    @Nonnull
    Collection<Node<Stmt, Fact>> elements();
}
