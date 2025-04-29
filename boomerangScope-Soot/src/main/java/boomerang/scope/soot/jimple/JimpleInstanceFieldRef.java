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
import boomerang.scope.InstanceFieldVal;
import boomerang.scope.Type;
import boomerang.scope.Val;
import java.util.Objects;
import soot.jimple.InstanceFieldRef;

public class JimpleInstanceFieldRef extends InstanceFieldVal {

  private final InstanceFieldRef delegate;
  private final JimpleMethod method;

  public JimpleInstanceFieldRef(InstanceFieldRef delegate, JimpleMethod method) {
    this(delegate, method, null);
  }

  private JimpleInstanceFieldRef(
      InstanceFieldRef delegate, JimpleMethod method, ControlFlowGraph.Edge unbalanced) {
    super(method, unbalanced);

    this.delegate = delegate;
    this.method = method;
  }

  public InstanceFieldRef getDelegate() {
    return delegate;
  }

  @Override
  public Val getBase() {
    return new JimpleVal(delegate.getBase(), method);
  }

  @Override
  public Field getField() {
    return new JimpleField(method.getScene(), delegate.getFieldRef());
  }

  @Override
  public Type getType() {
    return new JimpleType(method.getScene(), delegate.getType());
  }

  @Override
  public Val asUnbalanced(ControlFlowGraph.Edge stmt) {
    return new JimpleInstanceFieldRef(delegate, method, stmt);
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
    JimpleInstanceFieldRef that = (JimpleInstanceFieldRef) o;
    return Objects.equals(delegate.getBase(), that.delegate.getBase())
        && Objects.equals(delegate.getFieldRef(), that.delegate.getFieldRef());
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), delegate.getBase(), delegate.getFieldRef());
  }

  @Override
  public String toString() {
    return delegate.toString();
  }
}
