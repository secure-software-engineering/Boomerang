package boomerang.scope.sootup.jimple;

import boomerang.scope.Field;
import java.util.Arrays;
import sootup.java.core.JavaSootField;

public class JimpleUpField extends Field {

  private final JavaSootField delegate;

  public JimpleUpField(JavaSootField delegate) {
    this.delegate = delegate;
  }

  public JavaSootField getDelegate() {
    return delegate;
  }

  @Override
  public boolean isInnerClassField() {
    return delegate.getName().contains("$");
  }

  @Override
  public int hashCode() {
    return Arrays.hashCode(new Object[] {delegate});
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (!super.equals(obj)) return false;
    if (getClass() != obj.getClass()) return false;

    JimpleUpField other = (JimpleUpField) obj;
    if (delegate == null) {
      return other.delegate == null;
    } else return delegate.equals(other.delegate);
  }

  @Override
  public String toString() {
    return delegate.getName();
  }
}
