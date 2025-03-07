package boomerang.guided;

import boomerang.BackwardQuery;
import boomerang.ForwardQuery;
import boomerang.Query;
import boomerang.scope.ControlFlowGraph.Edge;
import boomerang.scope.Statement;
import boomerang.scope.Val;
import java.util.Collection;
import java.util.Collections;

public class ArrayContainerCollectionManager implements IDemandDrivenGuidedManager {

  @Override
  public Collection<Query> onForwardFlow(ForwardQuery query, Edge dataFlowEdge, Val dataFlowVal) {
    Statement targetStmt = dataFlowEdge.getStart();
    // Any statement of type someVariable[..] = rightOp
    if (targetStmt.isAssignStmt() && targetStmt.getLeftOp().isArrayRef()) {
      // If propagated fact also matches "someVariable"
      if (targetStmt.getLeftOp().getArrayBase().getX().equals(dataFlowVal)) {
        // Do start a new backward query for rightOp
        return Collections.singleton(BackwardQuery.make(dataFlowEdge, targetStmt.getRightOp()));
      }
    }
    return Collections.emptySet();
  }

  @Override
  public Collection<Query> onBackwardFlow(BackwardQuery query, Edge dataFlowEdge, Val dataFlowVal) {
    return Collections.emptySet();
  }
}
