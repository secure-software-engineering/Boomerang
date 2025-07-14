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
import boomerang.scope.InstanceFieldVal;
import boomerang.scope.Type;
import boomerang.scope.Val;
import java.util.Objects;
import sootup.core.jimple.common.ref.JInstanceFieldRef;

public class JimpleUpInstanceFieldRef extends InstanceFieldVal {

  private final JInstanceFieldRef delegate;
  private final JimpleUpMethod method;

  public JimpleUpInstanceFieldRef(JInstanceFieldRef delegate, JimpleUpMethod method) {
    this(delegate, method, null);
  }

  private JimpleUpInstanceFieldRef(
      JInstanceFieldRef delegate, JimpleUpMethod method, ControlFlowGraph.Edge unbalanced) {
    super(method, unbalanced);

    this.delegate = delegate;
    this.method = method;
  }

  public JInstanceFieldRef getDelegate() {
    return delegate;
  }

  @Override
  public Val getBase() {
    return new JimpleUpVal(delegate.getBase(), method);
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
    return new JimpleUpInstanceFieldRef(delegate, method, stmt);
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
    JimpleUpInstanceFieldRef that = (JimpleUpInstanceFieldRef) o;
    return Objects.equals(delegate, that.delegate);
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
