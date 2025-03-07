package boomerang.scope.soot.sparse.aliasaware;

import soot.AbstractUnit;
import soot.UnitPrinter;
import soot.Value;

public class DefinedOutside extends AbstractUnit {

  Value value;

  public DefinedOutside(Value value) {
    this.value = value;
  }

  public Value getValue() {
    return value;
  }

  @Override
  public Object clone() {
    return null;
  }

  @Override
  public boolean fallsThrough() {
    return false;
  }

  @Override
  public boolean branches() {
    return false;
  }

  @Override
  public void toString(UnitPrinter unitPrinter) {}
}
