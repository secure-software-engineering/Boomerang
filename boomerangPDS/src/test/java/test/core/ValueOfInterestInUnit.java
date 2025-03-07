package test.core;

import boomerang.Query;
import boomerang.scope.ControlFlowGraph.Edge;
import java.util.Optional;

public interface ValueOfInterestInUnit {
  Optional<? extends Query> test(Edge cfgEdge);
}
