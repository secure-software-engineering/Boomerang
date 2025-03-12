/**
 * ***************************************************************************** 
 * Copyright (c) 2025 Fraunhofer IEM, Paderborn, Germany. This program and the
 * accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at http://www.eclipse.org/legal/epl-2.0.
 *
 * <p>SPDX-License-Identifier: EPL-2.0
 *
 * <p>Contributors: Johannes Spaeth - initial API and implementation
 * *****************************************************************************
 */
package boomerang.scope.wala;

import boomerang.scope.DeclaredMethod;
import boomerang.scope.InvokeExpr;
import boomerang.scope.Type;
import boomerang.scope.WrappedClass;
import com.ibm.wala.types.MethodReference;
import java.util.List;

public class WALADeclaredMethod extends DeclaredMethod {

  private final MethodReference delegate;

  public WALADeclaredMethod(InvokeExpr inv, MethodReference ref) {
    super(inv);
    this.delegate = ref;
  }

  @Override
  public String getSubSignature() {
    return delegate.getName().toString();
  }

  @Override
  public String getName() {
    return delegate.getName().toString();
  }

  @Override
  public boolean isConstructor() {
    return delegate.isInit();
  }

  @Override
  public String getSignature() {
    return delegate.getSignature();
  }

  @Override
  public WrappedClass getDeclaringClass() {
    return new WALAClass(delegate.getDeclaringClass());
  }

  @Override
  public List<Type> getParameterTypes() {
    throw new UnsupportedOperationException("Not implemented yet");
  }

  @Override
  public Type getParameterType(int index) {
    throw new UnsupportedOperationException("Not implemented yet");
  }

  @Override
  public Type getReturnType() {
    throw new UnsupportedOperationException("Not implemented yet");
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((delegate == null) ? 0 : delegate.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    WALADeclaredMethod other = (WALADeclaredMethod) obj;
    if (delegate == null) {
      return other.delegate == null;
    } else return delegate.equals(other.delegate);
  }

  @Override
  public String toString() {
    return delegate.toString();
  }
}
