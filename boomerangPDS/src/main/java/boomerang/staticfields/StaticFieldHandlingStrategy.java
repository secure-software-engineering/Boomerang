package boomerang.staticfields;

import boomerang.scope.ControlFlowGraph.Edge;
import boomerang.scope.StaticFieldVal;
import boomerang.scope.Val;
import java.util.Set;
import wpds.interfaces.State;

public interface StaticFieldHandlingStrategy {

  void handleForward(Edge storeStmt, Val storedVal, StaticFieldVal staticVal, Set<State> out);

  void handleBackward(Edge curr, Val leftOp, StaticFieldVal staticField, Set<State> out);
}
