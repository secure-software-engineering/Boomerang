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

import boomerang.scope.ControlFlowGraph;
import boomerang.scope.Field;
import boomerang.scope.Method;
import boomerang.scope.StaticFieldVal;
import boomerang.scope.Type;
import boomerang.scope.Val;
import boomerang.scope.WrappedClass;
import java.util.Objects;
import soot.jimple.StaticFieldRef;

public class JimpleStaticFieldRef extends StaticFieldVal {

  private final StaticFieldRef delegate;

  public JimpleStaticFieldRef(StaticFieldRef delegate, Method method) {
    this(delegate, method, null);
  }

  private JimpleStaticFieldRef(
      StaticFieldRef delegate, Method method, ControlFlowGraph.Edge unbalanced) {
    super(method, unbalanced);

    this.delegate = delegate;
  }

  public StaticFieldRef getDelegate() {
    return delegate;
  }

  @Override
  public WrappedClass getDeclaringClass() {
    return new JimpleWrappedClass(delegate.getField().getDeclaringClass());
  }

  @Override
  public Field getField() {
    return new JimpleField(delegate.getFieldRef());
  }

  @Override
  public Type getType() {
    return new JimpleType(delegate.getType());
  }

  @Override
  public Val asUnbalanced(ControlFlowGraph.Edge stmt) {
    return new JimpleStaticFieldRef(delegate, m, stmt);
  }

  @Override
  public Val withNewMethod(Method callee) {
    return new JimpleStaticFieldRef(delegate, callee);
  }

  @Override
  public String getVariableName() {
    return delegate.toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    JimpleStaticFieldRef that = (JimpleStaticFieldRef) o;
    return Objects.equals(delegate.getFieldRef(), that.delegate.getFieldRef());
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), delegate.getFieldRef());
  }

  @Override
  public String toString() {
    return getVariableName();
  }
}
