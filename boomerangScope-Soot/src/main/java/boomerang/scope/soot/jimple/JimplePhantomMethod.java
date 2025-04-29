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

import boomerang.scope.PhantomMethod;
import boomerang.scope.Type;
import boomerang.scope.WrappedClass;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import soot.Scene;
import soot.SootMethod;
import soot.SootMethodRef;

/**
 * Class that wraps a {@link SootMethod} without an existing body. Operations that require
 * information from the body throw an exception.
 */
public class JimplePhantomMethod extends PhantomMethod {

  private final Scene scene;
  private final SootMethodRef delegate;

  public JimplePhantomMethod(Scene scene, SootMethodRef delegate) {
    this.scene = scene;
    this.delegate = delegate;
  }

  public Scene getScene() {
    return scene;
  }

  public SootMethodRef getDelegate() {
    return delegate;
  }

  @Override
  public boolean isStaticInitializer() {
    return delegate.getName().equals(SootMethod.staticInitializerName);
  }

  @Override
  public List<Type> getParameterTypes() {
    List<Type> types = new ArrayList<>();

    for (soot.Type type : delegate.getParameterTypes()) {
      types.add(new JimpleType(type, scene.getOrMakeFastHierarchy()));
    }
    return types;
  }

  @Override
  public Type getParameterType(int index) {
    return new JimpleType(delegate.getParameterType(index), scene.getOrMakeFastHierarchy());
  }

  @Override
  public Type getReturnType() {
    return new JimpleType(delegate.getReturnType(), scene.getOrMakeFastHierarchy());
  }

  @Override
  public boolean isStatic() {
    return delegate.isStatic();
  }

  @Override
  public WrappedClass getDeclaringClass() {
    return new JimpleWrappedClass(scene, delegate.getDeclaringClass());
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
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    JimplePhantomMethod that = (JimplePhantomMethod) o;
    return Objects.equals(delegate, that.delegate);
  }

  @Override
  public int hashCode() {
    return Objects.hash(delegate);
  }

  @Override
  public String toString() {
    return "PHANTOM:" + delegate.toString();
  }
}
