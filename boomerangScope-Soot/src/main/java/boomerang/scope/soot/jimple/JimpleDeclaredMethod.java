package boomerang.scope.soot.jimple;

import boomerang.scope.DeclaredMethod;
import boomerang.scope.InvokeExpr;
import boomerang.scope.Type;
import boomerang.scope.WrappedClass;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import soot.SootMethodRef;

public class JimpleDeclaredMethod extends DeclaredMethod {

  private final SootMethodRef delegate;

  public JimpleDeclaredMethod(InvokeExpr inv, SootMethodRef method) {
    super(inv);
    this.delegate = method;
  }

  @Override
  public String getSubSignature() {
    return delegate.getSubSignature().getString();
  }

  @Override
  public String getName() {
    return delegate.getName();
  }

  @Override
  public boolean isConstructor() {
    return delegate.isConstructor();
  }

  @Override
  public String getSignature() {
    return delegate.getSignature();
  }

  @Override
  public WrappedClass getDeclaringClass() {
    return new JimpleWrappedClass(delegate.getDeclaringClass());
  }

  @Override
  public List<Type> getParameterTypes() {
    List<Type> types = new ArrayList<>();

    for (soot.Type type : delegate.getParameterTypes()) {
      types.add(new JimpleType(type));
    }
    return types;
  }

  @Override
  public Type getParameterType(int index) {
    return new JimpleType(delegate.getParameterType(index));
  }

  @Override
  public Type getReturnType() {
    return new JimpleType(delegate.getReturnType());
  }

  public SootMethodRef getDelegate() {
    return delegate;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    JimpleDeclaredMethod that = (JimpleDeclaredMethod) o;
    return Objects.equals(delegate, that.delegate);
  }

  @Override
  public int hashCode() {
    return Objects.hash(delegate);
  }

  @Override
  public String toString() {
    return delegate.toString();
  }
}
