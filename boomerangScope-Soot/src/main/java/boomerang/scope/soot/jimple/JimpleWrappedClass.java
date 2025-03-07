package boomerang.scope.soot.jimple;

import boomerang.scope.Method;
import boomerang.scope.Type;
import boomerang.scope.WrappedClass;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import soot.SootClass;
import soot.SootMethod;

public class JimpleWrappedClass implements WrappedClass {

  private final SootClass delegate;
  private Collection<Method> methods;

  public JimpleWrappedClass(SootClass delegate) {
    this.delegate = delegate;
  }

  public Collection<Method> getMethods() {
    List<SootMethod> ms = delegate.getMethods();
    if (methods == null) {
      methods = Sets.newHashSet();
      for (SootMethod m : ms) {
        if (m.hasActiveBody()) methods.add(JimpleMethod.of(m));
      }
    }
    return methods;
  }

  public boolean hasSuperclass() {
    return delegate.hasSuperclass();
  }

  public WrappedClass getSuperclass() {
    return new JimpleWrappedClass(delegate.getSuperclass());
  }

  public Type getType() {
    return new JimpleType(delegate.getType());
  }

  public boolean isApplicationClass() {
    return delegate.isApplicationClass();
  }

  @Override
  public String getFullyQualifiedName() {
    return delegate.getName();
  }

  @Override
  public boolean isPhantom() {
    return delegate.isPhantom();
  }

  public SootClass getDelegate() {
    return delegate;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    JimpleWrappedClass that = (JimpleWrappedClass) o;
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
