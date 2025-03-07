package assertions;

import boomerang.scope.Statement;
import test.Assertion;

public class ShouldNotBeAnalyzed implements Assertion {

  private final Statement statement;
  private boolean unsound;

  public ShouldNotBeAnalyzed(Statement statement) {
    this.statement = statement;
    this.unsound = false;
  }

  public Statement getStatement() {
    return statement;
  }

  @Override
  public String toString() {
    return "Method should not be included in analysis: " + statement.getMethod();
  }

  @Override
  public boolean isUnsound() {
    return unsound;
  }

  @Override
  public boolean isImprecise() {
    return false;
  }

  @Override
  public String getAssertedMessage() {
    return toString();
  }

  public void hasBeenAnalyzed() {
    unsound = true;
  }
}
