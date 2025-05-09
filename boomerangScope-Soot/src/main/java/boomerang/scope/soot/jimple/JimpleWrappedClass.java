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

import boomerang.scope.Method;
import boomerang.scope.Type;
import boomerang.scope.WrappedClass;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;

public class JimpleWrappedClass implements WrappedClass {

  private final SootClass delegate;
  private final Scene scene;
  private Collection<Method> methods;

  public JimpleWrappedClass(SootClass delegate, Scene scene) {
    this.delegate = delegate;
    this.scene = scene;
  }

  public SootClass getDelegate() {
    return delegate;
  }

  public Collection<Method> getMethods() {
    List<SootMethod> ms = delegate.getMethods();
    if (methods == null) {
      methods = new LinkedHashSet<>();
      for (SootMethod m : ms) {
        if (m.hasActiveBody()) methods.add(JimpleMethod.of(m, scene));
      }
    }
    return methods;
  }

  public boolean hasSuperclass() {
    return delegate.hasSuperclass();
  }

  public WrappedClass getSuperclass() {
    return new JimpleWrappedClass(delegate.getSuperclass(), scene);
  }

  public Type getType() {
    return new JimpleType(delegate.getType(), scene);
  }

  public boolean isApplicationClass() {
    return delegate.isApplicationClass();
  }

  @Override
  public String getFullyQualifiedName() {
    return delegate.getName();
  }

  @Override
  public boolean isDefined() {
    return !delegate.isPhantom();
  }

  @Override
  public boolean isPhantom() {
    return delegate.isPhantom();
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
