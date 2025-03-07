package test.core;

import boomerang.Query;
import boomerang.scope.AnalysisScope;
import boomerang.scope.CallGraph;
import boomerang.scope.ControlFlowGraph.Edge;
import java.util.Collection;
import java.util.Collections;

public class Preanalysis extends AnalysisScope {

  private final ValueOfInterestInUnit f;

  public Preanalysis(CallGraph cg, ValueOfInterestInUnit f) {
    super(cg);
    this.f = f;
  }

  @Override
  protected Collection<? extends Query> generate(Edge seed) {
    if (f.test(seed).isPresent()) {
      return Collections.singleton(f.test(seed).get());
    }
    return Collections.emptySet();
  }
}
