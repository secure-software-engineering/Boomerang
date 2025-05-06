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

import boomerang.scope.ArrayVal;
import boomerang.scope.ControlFlowGraph;
import boomerang.scope.Type;
import boomerang.scope.Val;
import java.util.Objects;
import sootup.core.jimple.basic.Value;
import sootup.core.jimple.common.constant.IntConstant;
import sootup.core.jimple.common.ref.JArrayRef;

public class JimpleUpArrayRef extends ArrayVal {

  private final JArrayRef delegate;
  private final JimpleUpMethod method;

  public JimpleUpArrayRef(JArrayRef delegate, JimpleUpMethod method) {
    this(delegate, method, null);
  }

  public JimpleUpArrayRef(
      JArrayRef delegate, JimpleUpMethod method, ControlFlowGraph.Edge unbalanced) {
    super(method, unbalanced);

    this.delegate = delegate;
    this.method = method;
  }

  public JArrayRef getDelegate() {
    return delegate;
  }

  @Override
  public Val getBase() {
    return new JimpleUpVal(delegate.getBase(), method);
  }

  @Override
  public Val getIndexExpr() {
    return new JimpleUpVal(delegate.getIndex(), method);
  }

  @Override
  public int getIndex() {
    Value indexExpr = delegate.getIndex();

    if (indexExpr instanceof IntConstant) {
      return ((IntConstant) indexExpr).getValue();
    } else {
      return -1;
    }
  }

  @Override
  public Type getType() {
    return new JimpleUpType(delegate.getType(), method.getView());
  }

  @Override
  public Val asUnbalanced(ControlFlowGraph.Edge stmt) {
    return new JimpleUpArrayRef(delegate, method, stmt);
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
    JimpleUpArrayRef that = (JimpleUpArrayRef) o;
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
