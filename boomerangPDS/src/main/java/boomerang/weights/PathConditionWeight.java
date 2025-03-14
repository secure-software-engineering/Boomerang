package boomerang.weights;

import boomerang.scope.Statement;
import boomerang.scope.Val;
import wpds.impl.Weight;

import java.util.Map;

public interface PathConditionWeight extends Weight {
    Map<Statement, ConditionDomain> getConditions();

    Map<Val, ConditionDomain> getEvaluationMap();

    public enum ConditionDomain {
        TRUE,
        FALSE,
        TOP
    }
}
