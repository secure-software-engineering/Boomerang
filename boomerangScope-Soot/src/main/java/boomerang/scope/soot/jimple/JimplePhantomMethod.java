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
package boomerang.scope.soot.jimple;

import boomerang.scope.ControlFlowGraph;
import boomerang.scope.Method;
import boomerang.scope.Statement;
import boomerang.scope.Type;
import boomerang.scope.Val;
import boomerang.scope.WrappedClass;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import soot.SootMethod;

/**
 * Class that wraps a {@link SootMethod} without an existing body. Operations that require
 * information from the body throw an exception.
 */
public class JimplePhantomMethod extends Method {

  private final SootMethod delegate;

  protected JimplePhantomMethod(SootMethod delegate) {
    this.delegate = delegate;
    if (delegate.hasActiveBody()) {
      throw new RuntimeException("Building phantom method " + delegate + " with existing body");
    }
  }

  public static JimplePhantomMethod of(SootMethod delegate) {
    return new JimplePhantomMethod(delegate);
  }

  @Override
  public boolean isStaticInitializer() {
    return delegate.isStaticInitializer();
  }

  @Override
  public boolean isParameterLocal(Val val) {
    return false;
  }

  @Override
  public List<Type> getParameterTypes() {
    List<Type> types = new ArrayList<>();

    for (soot.Type type : delegate.getParameterTypes()) {
      types.add(new JimpleType(type));
    }
    return types;
  }

  @Override
  public Type getParameterType(int index) {
    return new JimpleType(delegate.getParameterType(index));
  }

  @Override
  public Type getReturnType() {
    return new JimpleType(delegate.getReturnType());
  }

  @Override
  public boolean isThisLocal(Val val) {
    return false;
  }

  @Override
  public Collection<Val> getLocals() {
    throw new RuntimeException("Locals of phantom method are not available");
  }

  @Override
  public Val getThisLocal() {
    throw new RuntimeException("this local of phantom method is not available");
  }

  @Override
  public List<Val> getParameterLocals() {
    throw new RuntimeException(
        "Parameter locals of phantom method " + delegate + " are not available");
  }

  @Override
  public boolean isStatic() {
    return delegate.isStatic();
  }

  @Override
  public boolean isDefined() {
    return false;
  }

  @Override
  public boolean isPhantom() {
    return true;
  }

  @Override
  public List<Statement> getStatements() {
    throw new RuntimeException("Statements of phantom method " + delegate + " are not available");
  }

  @Override
  public WrappedClass getDeclaringClass() {
    return new JimpleWrappedClass(delegate.getDeclaringClass());
  }

  @Override
  public ControlFlowGraph getControlFlowGraph() {
    throw new RuntimeException("CFG of phantom method " + delegate + " is not available");
  }

  @Override
  public String getSubSignature() {
    return delegate.getSubSignature();
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
