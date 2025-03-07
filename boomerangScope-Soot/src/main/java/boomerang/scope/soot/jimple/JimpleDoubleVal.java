package boomerang.scope.soot.jimple;

import boomerang.scope.Method;
import boomerang.scope.Val;
import boomerang.scope.ValWithFalseVariable;
import java.util.Objects;
import soot.Value;

// TODO May be removed
public class JimpleDoubleVal extends JimpleVal implements ValWithFalseVariable {

  private final Val falseVariable;

  public JimpleDoubleVal(Value v, Method m, Val instanceofValue) {
    super(v, m);
    this.falseVariable = instanceofValue;
  }

  public Val getFalseVariable() {
    return falseVariable;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    JimpleDoubleVal that = (JimpleDoubleVal) o;
    return Objects.equals(falseVariable, that.falseVariable);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), falseVariable);
  }

  @Override
  public String toString() {
    return "InstanceOf " + falseVariable + " " + super.toString();
  }
}
