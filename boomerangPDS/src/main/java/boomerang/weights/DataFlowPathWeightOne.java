package boomerang.weights;

import boomerang.scope.ControlFlowGraph;
import boomerang.scope.Statement;
import boomerang.scope.Val;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nonnull;
import sync.pds.solver.nodes.Node;
import wpds.impl.Weight;

public class DataFlowPathWeightOne implements DataFlowPathWeight {

  @Nonnull private static final DataFlowPathWeightOne one = new DataFlowPathWeightOne();

  public DataFlowPathWeightOne() {}

  public static DataFlowPathWeightOne one() {
    return one;
  }

  @Nonnull
  @Override
  public Set<Node<ControlFlowGraph.Edge, Val>> getAllStatements() {
    throw new IllegalStateException("MinDistanceWeight.getAllStatements() - don't");
  }

  @Nonnull
  @Override
  public Map<Statement, PathConditionWeight.ConditionDomain> getConditions() {
    throw new IllegalStateException("MinDistanceWeight.getAllStatements() - don't");
  }

  @Nonnull
  @Override
  public Map<Val, PathConditionWeight.ConditionDomain> getEvaluationMap() {
    throw new IllegalStateException("MinDistanceWeight.getEvaluationMap() - don't");
  }

  @Nonnull
  @Override
  public Weight extendWith(@Nonnull Weight other) {
    throw new IllegalStateException("MinDistanceWeight.extendWith() - don't");
  }

  @Nonnull
  @Override
  public Weight combineWith(@Nonnull Weight other) {
    throw new IllegalStateException("MinDistanceWeight.combineWith() - don't");
  }
}
