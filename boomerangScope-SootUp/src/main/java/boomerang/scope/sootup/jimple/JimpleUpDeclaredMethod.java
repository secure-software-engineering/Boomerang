/**
 * ***************************************************************************** 
 * Copyright (c) 2018 Fraunhofer IEM, Paderborn, Germany
 * <p>
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * <p>
 * SPDX-License-Identifier: EPL-2.0
 * <p>
 * Contributors:
 *   Johannes Spaeth - initial API and implementation
 * *****************************************************************************
 */
package boomerang.scope.sootup.jimple;

import boomerang.scope.DeclaredMethod;
import boomerang.scope.Type;
import boomerang.scope.WrappedClass;
import boomerang.scope.sootup.SootUpFrameworkScope;
import boomerang.utils.MethodWrapper;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import sootup.core.signatures.MethodSignature;

public class JimpleUpDeclaredMethod extends DeclaredMethod {

  private final MethodSignature delegate;
  private final JimpleUpMethod method;

  public JimpleUpDeclaredMethod(
      JimpleUpInvokeExpr invokeExpr, MethodSignature delegate, JimpleUpMethod method) {
    super(invokeExpr);

    this.delegate = delegate;
    this.method = method;
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
    return new JimpleUpWrappedClass(delegate.getDeclClassType(), method.getView());
  }

  @Override
  public List<Type> getParameterTypes() {
    return delegate.getParameterTypes().stream()
        .map(p -> new JimpleUpType(p, method.getView()))
        .collect(Collectors.toList());
  }

  @Override
  public Type getParameterType(int index) {
    return getParameterTypes().get(index);
  }

  @Override
  public Type getReturnType() {
    return new JimpleUpType(delegate.getType(), method.getView());
  }

  @Override
  public MethodWrapper toMethodWrapper() {
    List<String> paramTypes =
        delegate.getParameterTypes().stream().map(Object::toString).collect(Collectors.toList());

    return new MethodWrapper(
        delegate.getDeclClassType().getFullyQualifiedName(),
        delegate.getName(),
        delegate.getType().toString(),
        paramTypes);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    JimpleUpDeclaredMethod that = (JimpleUpDeclaredMethod) o;
    return Objects.equals(delegate, that.delegate);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), delegate);
  }

  @Override
  public String toString() {
    return delegate.toString();
  }
}
