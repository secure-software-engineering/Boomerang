package boomerang.weights;

import boomerang.scope.ControlFlowGraph;
import boomerang.scope.Statement;
import boomerang.scope.Val;
import sync.pds.solver.nodes.Node;
import wpds.impl.Weight;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.Set;

public class DataFlowPathWeightOne implements DataFlowPathWeightImpl{


    @Nonnull
    private static final DataFlowPathWeightOne one =
            new DataFlowPathWeightOne();

    public DataFlowPathWeightOne() {}

    public static DataFlowPathWeightOne one() {
        return one;
    }

    @Override
    public Set<Node<ControlFlowGraph.Edge, Val>> getAllStatements() {
        return Set.of();
    }

    @Override
    public Map<Statement, PathConditionWeight.ConditionDomain> getConditions() {
        return Map.of();
    }

    @Override
    public Map<Val, PathConditionWeight.ConditionDomain> getEvaluationMap() {
        return Map.of();
    }

    @Nonnull
    @Override
    public Weight extendWith(@Nonnull Weight other) {
        return null;
    }

    @Nonnull
    @Override
    public Weight combineWith(@Nonnull Weight other) {
        return null;
    }
}
