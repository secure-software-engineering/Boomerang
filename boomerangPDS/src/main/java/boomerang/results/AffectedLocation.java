package boomerang.results;

import boomerang.scope.ControlFlowGraph;
import boomerang.scope.Val;
import java.util.List;

public interface AffectedLocation {

  ControlFlowGraph.Edge getStatement();

  Val getVariable();

  List<PathElement> getDataFlowPath();

  String getMessage();

  int getRuleIndex();
}
