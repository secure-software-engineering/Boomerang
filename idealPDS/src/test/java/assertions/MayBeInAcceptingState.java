package assertions;

import boomerang.scope.Statement;
import boomerang.scope.Val;
import java.util.Collection;
import typestate.finiteautomata.State;

public class MayBeInAcceptingState extends StateResult {

  private boolean satisfied;
  private boolean checked;

  public MayBeInAcceptingState(Statement statement, Val seed) {
    super(statement, seed);

    this.satisfied = false;
    this.checked = false;
  }

  @Override
  public void computedStates(Collection<State> states) {
    // Check if any state is accepting
    for (State state : states) {
      satisfied |= state.isAccepting();
    }
    checked = true;
  }

  @Override
  public boolean isUnsound() {
    return !checked || !satisfied;
  }

  @Override
  public String getAssertedMessage() {
    if (checked) {
      return seed.getVariableName()
          + " is expected to be in an accepting state @ "
          + statement
          + " @ line "
          + statement.getStartLineNumber();
    } else {
      return statement + " @ line " + statement.getStartLineNumber() + " has not been checked";
    }
  }
}
