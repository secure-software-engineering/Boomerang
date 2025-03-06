package typestate;

import boomerang.scope.ControlFlowGraph;
import java.util.Collection;
import java.util.Set;
import javax.annotation.Nonnull;
import typestate.finiteautomata.ITransition;
import wpds.impl.Weight;

public interface TransitionFunction extends Weight {
  @Nonnull
  Collection<ITransition> getValues();

  @Nonnull
  Set<ControlFlowGraph.Edge> getStateChangeStatements();

  @Nonnull
  Weight extendWith(@Nonnull Weight other);

  @Nonnull
  Weight combineWith(@Nonnull Weight other);
}
