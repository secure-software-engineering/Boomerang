package boomerang.weights;

import boomerang.scope.ControlFlowGraph;
import boomerang.scope.Statement;
import boomerang.scope.Val;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nonnull;
import sync.pds.solver.nodes.Node;
import wpds.impl.Weight;

public interface DataFlowPathWeight extends Weight {
  Set<Node<ControlFlowGraph.Edge, Val>> getAllStatements();

  Map<Statement, PathConditionWeight.ConditionDomain> getConditions();

  Map<Val, PathConditionWeight.ConditionDomain> getEvaluationMap();

  @Nonnull
  Weight extendWith(@Nonnull Weight other);

  @Nonnull
  Weight combineWith(@Nonnull Weight other);
}
