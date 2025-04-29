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
package boomerang.scope.soot.jimple;

import boomerang.scope.DeclaredMethod;
import boomerang.scope.InvokeExpr;
import boomerang.scope.Type;
import boomerang.scope.WrappedClass;
import boomerang.utils.MethodWrapper;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import soot.SootMethodRef;

public class JimpleDeclaredMethod extends DeclaredMethod {

  private final SootMethodRef delegate;
  private final JimpleMethod method;

  public JimpleDeclaredMethod(InvokeExpr inv, SootMethodRef delegate, JimpleMethod method) {
    super(inv);
    this.delegate = delegate;
    this.method = method;
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
    return new JimpleWrappedClass(method.getScene(), delegate.getDeclaringClass());
  }

  @Override
  public List<Type> getParameterTypes() {
    List<Type> types = new ArrayList<>();

    for (soot.Type type : delegate.getParameterTypes()) {
      types.add(new JimpleType(method.getScene(), type));
    }
    return types;
  }

  @Override
  public Type getParameterType(int index) {
    return new JimpleType(method.getScene(), delegate.getParameterType(index));
  }

  @Override
  public Type getReturnType() {
    return new JimpleType(method.getScene(), delegate.getReturnType());
  }

  @Override
  public MethodWrapper toMethodWrapper() {
    List<String> paramTypes =
        delegate.getParameterTypes().stream().map(soot.Type::toString).collect(Collectors.toList());

    return new MethodWrapper(
        delegate.getDeclaringClass().getName(),
        delegate.getName(),
        delegate.getReturnType().toString(),
        paramTypes);
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
