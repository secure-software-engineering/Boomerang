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
  @Nonnull Set<Node<ControlFlowGraph.Edge, Val>> getAllStatements();

  @Nonnull Map<Statement, PathConditionWeight.ConditionDomain> getConditions();

  @Nonnull Map<Val, PathConditionWeight.ConditionDomain> getEvaluationMap();

}
