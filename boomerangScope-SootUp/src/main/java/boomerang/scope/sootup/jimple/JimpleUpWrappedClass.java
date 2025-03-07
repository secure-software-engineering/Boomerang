package boomerang.scope.sootup.jimple;

import boomerang.scope.Method;
import boomerang.scope.Type;
import boomerang.scope.WrappedClass;
import boomerang.scope.sootup.SootUpFrameworkScope;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import sootup.java.core.JavaSootClass;
import sootup.java.core.JavaSootMethod;
import sootup.java.core.types.JavaClassType;

public class JimpleUpWrappedClass implements WrappedClass {

  private final JavaSootClass delegate;
  private Collection<Method> methodsCache;

  public JimpleUpWrappedClass(JavaSootClass delegate) {
    this.delegate = delegate;
  }

  @Override
  public Collection<Method> getMethods() {
    if (methodsCache == null) {
      methodsCache = new HashSet<>();

      for (JavaSootMethod method : delegate.getMethods()) {
        if (method.hasBody()) {
          methodsCache.add(JimpleUpMethod.of(method));
        }
      }
    }
    return methodsCache;
  }

  @Override
  public boolean hasSuperclass() {
    return delegate.hasSuperclass();
  }

  @Override
  public WrappedClass getSuperclass() {
    Optional<JavaClassType> superClassType = delegate.getSuperclass();
    if (superClassType.isEmpty()) {
      throw new RuntimeException("Super class type of " + superClassType + " is not present");
    }
    JavaSootClass superClass =
        SootUpFrameworkScope.getInstance().getSootClass(superClassType.get());
    return new JimpleUpWrappedClass(superClass);
  }

  @Override
  public Type getType() {
    return new JimpleUpType(delegate.getType());
  }

  @Override
  public boolean isApplicationClass() {
    return delegate.isApplicationClass();
  }

  @Override
  public String getFullyQualifiedName() {
    return delegate.getName();
  }

  @Override
  public boolean isPhantom() {
    // TODO May change
    return false;
  }

  public JavaSootClass getDelegate() {
    return delegate;
  }

  @Override
  public int hashCode() {
    return Arrays.hashCode(new Object[] {delegate});
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;

    JimpleUpWrappedClass other = (JimpleUpWrappedClass) obj;
    if (delegate == null) {
      return other.delegate == null;
    } else return delegate.equals(other.delegate);
  }

  @Override
  public String toString() {
    return delegate.getName();
  }
}
