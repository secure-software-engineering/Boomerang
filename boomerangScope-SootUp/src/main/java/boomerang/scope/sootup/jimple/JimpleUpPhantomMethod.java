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

import boomerang.scope.PhantomMethod;
import boomerang.scope.Type;
import boomerang.scope.WrappedClass;
import boomerang.scope.sootup.SootUpFrameworkScope;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import sootup.java.core.JavaSootMethod;
import sootup.java.core.views.JavaView;

public class JimpleUpPhantomMethod extends PhantomMethod {

  private final JavaView view;
  private final JavaSootMethod delegate;

  public JimpleUpPhantomMethod(JavaView view, JavaSootMethod delegate) {
    this.view = view;
    this.delegate = delegate;

    if (delegate.hasBody()) {
      throw new IllegalArgumentException("Cannot build phantom method from method with body");
    }
  }

  public JavaSootMethod getDelegate() {
    return delegate;
  }

  @Override
  public boolean isStaticInitializer() {
    return delegate.getName().equals(SootUpFrameworkScope.STATIC_INITIALIZER_NAME);
  }

  @Override
  public List<Type> getParameterTypes() {
    List<Type> result = new ArrayList<>();

    for (sootup.core.types.Type type : delegate.getParameterTypes()) {
      result.add(new JimpleUpType(view, type));
    }

    return result;
  }

  @Override
  public Type getParameterType(int index) {
    return new JimpleUpType(view, delegate.getParameterType(index));
  }

  @Override
  public Type getReturnType() {
    return new JimpleUpType(view, delegate.getReturnType());
  }

  @Override
  public boolean isStatic() {
    return delegate.isStatic();
  }

  @Override
  public WrappedClass getDeclaringClass() {
    return new JimpleUpWrappedClass(view, delegate.getDeclaringClassType());
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
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    JimpleUpPhantomMethod that = (JimpleUpPhantomMethod) o;
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
