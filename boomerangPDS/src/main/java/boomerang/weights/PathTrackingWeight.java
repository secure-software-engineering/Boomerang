package boomerang.weights;

import boomerang.scope.ControlFlowGraph;
import boomerang.scope.Val;
import java.util.Set;
import javax.annotation.Nonnull;
import sync.pds.solver.nodes.Node;
import wpds.impl.Weight;

public interface PathTrackingWeight extends Weight {
  @Nonnull
  Set<Node<ControlFlowGraph.Edge, Val>> getShortestPathWitness();
}
