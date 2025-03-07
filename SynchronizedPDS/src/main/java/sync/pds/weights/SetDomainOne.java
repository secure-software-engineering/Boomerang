package sync.pds.weights;


import sync.pds.solver.nodes.Node;
import wpds.impl.Weight;

import javax.annotation.Nonnull;
import java.util.Collection;


public class SetDomainOne implements SetDomainInterface{
    @Nonnull
    private static final SetDomainOne one =
            new SetDomainOne();

    public static Weight one() {
        return one;
    }

    @Nonnull
    @Override
    public Weight extendWith(@Nonnull Weight other) {
        if (other == one()) {
            return this;
        }
        return other;
    }

    @Nonnull
    @Override
    public Weight combineWith(@Nonnull Weight other) {
        if (!(other instanceof SetDomainOne)) {
            throw new RuntimeException("SetDomainOne.combineWith() - don't");
        }
        return other;
    }

    @Nonnull
    @Override
    public Collection<Node> getNodes() {
        throw new IllegalStateException("SetDomianOne.nodes() - don't");
    }

    @Nonnull
    @Override
    public Collection<Node> elements() {
        throw new IllegalStateException("SetDomianOne.elements() - don't");

    }
    public String toString() {
        return "ONE";
    }
}
