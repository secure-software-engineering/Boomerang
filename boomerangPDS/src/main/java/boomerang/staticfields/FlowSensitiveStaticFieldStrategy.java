package boomerang.staticfields;

import boomerang.scope.ControlFlowGraph.Edge;
import boomerang.scope.StaticFieldVal;
import boomerang.scope.Val;
import java.util.Set;
import sync.pds.solver.nodes.Node;
import wpds.interfaces.State;

public class FlowSensitiveStaticFieldStrategy implements StaticFieldHandlingStrategy {
  @Override
  public void handleForward(
      Edge storeStmt, Val storedVal, StaticFieldVal staticVal, Set<State> out) {
    out.add(new Node<>(storeStmt, staticVal));
  }

  @Override
  public void handleBackward(
      Edge loadStatement, Val loadedVal, StaticFieldVal staticVal, Set<State> out) {
    out.add(new Node<>(loadStatement, loadStatement.getTarget().getStaticField()));
  }
}
