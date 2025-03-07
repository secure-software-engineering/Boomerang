package boomerang.scope.sootup.jimple;

import boomerang.scope.DeclaredMethod;
import boomerang.scope.Type;
import boomerang.scope.WrappedClass;
import boomerang.scope.sootup.SootUpFrameworkScope;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import sootup.core.signatures.MethodSignature;
import sootup.java.core.JavaSootClass;
import sootup.java.core.types.JavaClassType;

public class JimpleUpDeclaredMethod extends DeclaredMethod {

  private final MethodSignature delegate;

  public JimpleUpDeclaredMethod(JimpleUpInvokeExpr invokeExpr, MethodSignature delegate) {
    super(invokeExpr);

    this.delegate = delegate;
  }

  public MethodSignature getDelegate() {
    return delegate;
  }

  @Override
  public String getSubSignature() {
    return delegate.getSubSignature().toString();
  }

  @Override
  public String getName() {
    return delegate.getName();
  }

  @Override
  public boolean isConstructor() {
    return delegate.getName().equals(SootUpFrameworkScope.CONSTRUCTOR_NAME);
  }

  @Override
  public String getSignature() {
    return delegate.toString();
  }

  @Override
  public WrappedClass getDeclaringClass() {
    JavaSootClass sootClass =
        SootUpFrameworkScope.getInstance()
            .getSootClass((JavaClassType) delegate.getDeclClassType());
    return new JimpleUpWrappedClass(sootClass);
  }

  @Override
  public List<Type> getParameterTypes() {
    return delegate.getParameterTypes().stream()
        .map(JimpleUpType::new)
        .collect(Collectors.toList());
  }

  @Override
  public Type getParameterType(int index) {
    return getParameterTypes().get(index);
  }

  @Override
  public Type getReturnType() {
    return new JimpleUpType(delegate.getType());
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

    JimpleUpDeclaredMethod other = (JimpleUpDeclaredMethod) obj;
    if (delegate == null) {
      return other.delegate == null;
    } else return delegate.equals(other.delegate);
  }

  @Override
  public String toString() {
    return delegate.toString();
  }
}
