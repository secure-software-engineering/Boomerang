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

import boomerang.scope.ControlFlowGraph;
import boomerang.scope.Field;
import boomerang.scope.Method;
import boomerang.scope.StaticFieldVal;
import boomerang.scope.Type;
import boomerang.scope.Val;
import boomerang.scope.WrappedClass;
import java.util.Objects;
import sootup.core.jimple.common.ref.JStaticFieldRef;

public class JimpleUpStaticFieldRef extends StaticFieldVal {

  private final JStaticFieldRef delegate;
  private final JimpleUpMethod method;
  private final int hashCode;

  public JimpleUpStaticFieldRef(JStaticFieldRef delegate, JimpleUpMethod method) {
    this(delegate, method, null);
  }

  private JimpleUpStaticFieldRef(
      JStaticFieldRef delegate, JimpleUpMethod method, ControlFlowGraph.Edge unbalanced) {
    super(method, unbalanced);

    this.delegate = delegate;
    this.method = method;
    // TODO(SootUp > 3.0.1): JFieldRef overrides equals (comparing the field signatures) but not
    //  hashCode, i.e. equal delegates may have different hash codes. Thus, equality is based on the
    //  field signature instead of the delegate. Once SootUp fixes hashCode, replace this with
    //  Objects.hash(super.hashCode(), delegate) and compare the delegates in equals.
    this.hashCode = Objects.hash(super.hashCode(), delegate.getFieldSignature());
  }

  public JStaticFieldRef getDelegate() {
    return delegate;
  }

  @Override
  public WrappedClass getDeclaringClass() {
    return new JimpleUpWrappedClass(
        delegate.getFieldSignature().getDeclClassType(), method.getView());
  }

  @Override
  public Field getField() {
    return new JimpleUpField(delegate.getFieldSignature());
  }

  @Override
  public Type getType() {
    return new JimpleUpType(delegate.getType(), method.getView());
  }

  @Override
  public Val asUnbalanced(ControlFlowGraph.Edge stmt) {
    return new JimpleUpStaticFieldRef(delegate, method, stmt);
  }

  @Override
  public Val withNewMethod(Method callee) {
    if (callee instanceof JimpleUpMethod) {
      return new JimpleUpStaticFieldRef(delegate, (JimpleUpMethod) callee);
    }

    throw new RuntimeException("Cannot apply method that is not a JimpleUpMethod");
  }

  @Override
  public String getVariableName() {
    return delegate.toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    JimpleUpStaticFieldRef that = (JimpleUpStaticFieldRef) o;
    if (hashCode != that.hashCode) return false;
    if (!super.equals(o)) return false;
    return delegate.getFieldSignature().equals(that.delegate.getFieldSignature());
  }

  @Override
  public int hashCode() {
    return hashCode;
  }

  @Override
  public String toString() {
    return delegate.toString();
  }
}
