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

import boomerang.scope.ArrayVal;
import boomerang.scope.ControlFlowGraph;
import boomerang.scope.Type;
import boomerang.scope.Val;
import java.util.Objects;
import soot.Value;
import soot.jimple.ArrayRef;
import soot.jimple.IntConstant;

public class JimpleArrayRef extends ArrayVal {

  private final ArrayRef delegate;
  private final JimpleMethod method;

  public JimpleArrayRef(ArrayRef delegate, JimpleMethod method) {
    this(delegate, method, null);
  }

  private JimpleArrayRef(ArrayRef delegate, JimpleMethod method, ControlFlowGraph.Edge unbalanced) {
    super(method, unbalanced);

    this.delegate = delegate;
    this.method = method;
  }

  public ArrayRef getDelegate() {
    return delegate;
  }

  @Override
  public Val getBase() {
    return new JimpleVal(delegate.getBase(), method);
  }

  @Override
  public Val getIndexExpr() {
    return new JimpleVal(delegate.getIndex(), method);
  }

  @Override
  public int getIndex() {
    Value indexExpr = delegate.getIndex();

    if (indexExpr instanceof IntConstant) {
      return ((IntConstant) indexExpr).value;
    } else {
      return -1;
    }
  }

  @Override
  public Type getType() {
    return new JimpleType(delegate.getType(), method.getScene());
  }

  @Override
  public Val asUnbalanced(ControlFlowGraph.Edge stmt) {
    return new JimpleArrayRef(delegate, method, stmt);
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
    JimpleArrayRef that = (JimpleArrayRef) o;
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
