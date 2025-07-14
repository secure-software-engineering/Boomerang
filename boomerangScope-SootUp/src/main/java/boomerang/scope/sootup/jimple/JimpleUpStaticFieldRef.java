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

  public JimpleUpStaticFieldRef(JStaticFieldRef delegate, JimpleUpMethod method) {
    this(delegate, method, null);
  }

  private JimpleUpStaticFieldRef(
      JStaticFieldRef delegate, JimpleUpMethod method, ControlFlowGraph.Edge unbalanced) {
    super(method, unbalanced);

    this.delegate = delegate;
    this.method = method;
  }

  @Override
  public WrappedClass getDeclaringClass() {
    return new JimpleUpWrappedClass(
        delegate.getFieldSignature().getDeclClassType(), method.getView());
  }

  @Override
  public Field getField() {
    return new JimpleUpField(delegate.getFieldSignature(), method.getView());
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
    if (!super.equals(o)) return false;
    JimpleUpStaticFieldRef that = (JimpleUpStaticFieldRef) o;
    // TODO
    //  Wrong equals implementation in SootUp. Once fixed, replace this with
    //  the commented line
    return (delegate != null && delegate.equals(that.delegate));
    // return Objects.equals(delegate, that.delegate);
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
