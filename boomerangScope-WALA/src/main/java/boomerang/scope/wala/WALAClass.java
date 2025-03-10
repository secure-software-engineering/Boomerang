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

import boomerang.scope.Method;
import boomerang.scope.Type;
import boomerang.scope.WrappedClass;
import com.ibm.wala.cast.java.ipa.callgraph.JavaSourceAnalysisScope;
import com.ibm.wala.types.ClassLoaderReference;
import com.ibm.wala.types.TypeReference;
import java.util.Collection;

public class WALAClass implements WrappedClass {

  private final TypeReference delegate;

  public WALAClass(TypeReference typeReference) {
    this.delegate = typeReference;
  }

  @Override
  public Collection<Method> getMethods() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public boolean hasSuperclass() {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public WrappedClass getSuperclass() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Type getType() {
    return null;
  }

  @Override
  public boolean isApplicationClass() {
    return delegate.getClassLoader().equals(ClassLoaderReference.Application)
        || delegate.getClassLoader().equals(JavaSourceAnalysisScope.SOURCE);
  }

  @Override
  public boolean isPhantom() {
    return false;
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
    WALAClass other = (WALAClass) obj;
    if (delegate == null) {
      return other.delegate == null;
    } else return delegate.equals(other.delegate);
  }

  @Override
  public String getFullyQualifiedName() {
    if (delegate.getName() == null) return "FAILED";
    if (delegate.getName().getPackage() == null) {
      return delegate.getName().getClassName().toString();
    }
    return delegate.getName().getPackage().toString().replace("/", ".")
        + "."
        + delegate.getName().getClassName();
  }

  public TypeReference getDelegate() {
    return delegate;
  }
}
