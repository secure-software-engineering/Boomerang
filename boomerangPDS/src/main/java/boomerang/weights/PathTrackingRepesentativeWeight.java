package boomerang.weights;

import boomerang.scope.ControlFlowGraph.Edge;
import boomerang.scope.Val;
import sync.pds.solver.nodes.Node;
import wpds.impl.Weight;

import javax.annotation.Nonnull;
import java.util.List;

public class PathTrackingRepesentativeWeight implements PathTrackingWeight {

    private static PathTrackingRepesentativeWeight one;

    private PathTrackingRepesentativeWeight() {
        /*  Singleton */
    }

    public static PathTrackingRepesentativeWeight one() {
        if (one == null) {
            one = new PathTrackingRepesentativeWeight();
        }
        return one;
    }

    @Override
    @Nonnull
    public Weight extendWith(@Nonnull Weight o) {
        throw new IllegalStateException("This should not happen!");
    }

    @Override
    @Nonnull
    public Weight combineWith(@Nonnull Weight o) {
        throw new IllegalStateException("This should not happen!");
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return this == one;
    }

    @Override
    public String toString() {
        return "ONE";
    }

    @Nonnull
    @Override
    public List<Node<Edge, Val>> getShortestPathWitness() {
        throw new IllegalStateException("don't!");
    }
}
