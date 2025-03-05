package boomerang.weights;

import boomerang.scope.ControlFlowGraph;
import boomerang.scope.Val;
import sync.pds.solver.nodes.Node;
import wpds.impl.Weight;

import javax.annotation.Nonnull;
import java.util.List;

public interface PathTrackingWeight extends Weight {
    @Nonnull
    List<Node<ControlFlowGraph.Edge, Val>> getShortestPathWitness();
}
