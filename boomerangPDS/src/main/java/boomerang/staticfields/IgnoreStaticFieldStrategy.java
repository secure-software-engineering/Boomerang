package boomerang.staticfields;

import boomerang.scope.ControlFlowGraph.Edge;
import boomerang.scope.StaticFieldVal;
import boomerang.scope.Val;
import java.util.Set;
import wpds.interfaces.State;

public class IgnoreStaticFieldStrategy implements StaticFieldHandlingStrategy {

  @Override
  public void handleForward(
      Edge storeStmt, Val storedVal, StaticFieldVal staticVal, Set<State> out) {}

  @Override
  public void handleBackward(
      Edge loadStatement, Val loadedVal, StaticFieldVal staticVal, Set<State> out) {}
}
