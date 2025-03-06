package typestate;

import boomerang.scope.ControlFlowGraph;
import java.util.Collection;
import java.util.Set;
import javax.annotation.Nonnull;
import typestate.finiteautomata.Transition;
import wpds.impl.Weight;

public interface TransitionFunction extends Weight {
  @Nonnull
  Collection<Transition> getValues();

  @Nonnull
  Set<ControlFlowGraph.Edge> getStateChangeStatements();

  @Nonnull
  Weight extendWith(@Nonnull Weight other);

  @Nonnull
  Weight combineWith(@Nonnull Weight other);
}
