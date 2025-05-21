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

import boomerang.scope.ControlFlowGraph.Edge;
import boomerang.scope.IArrayRef;
import boomerang.scope.Method;
import boomerang.scope.Type;
import boomerang.scope.Val;
import java.util.Objects;
import soot.Local;
import soot.Value;
import soot.jimple.ArrayRef;
import soot.jimple.CastExpr;
import soot.jimple.ClassConstant;
import soot.jimple.InstanceOfExpr;
import soot.jimple.IntConstant;
import soot.jimple.LengthExpr;
import soot.jimple.LongConstant;
import soot.jimple.NewArrayExpr;
import soot.jimple.NewExpr;
import soot.jimple.NewMultiArrayExpr;
import soot.jimple.NullConstant;
import soot.jimple.StringConstant;

public class JimpleVal extends Val {

  private final Value delegate;
  private final JimpleMethod method;

  public JimpleVal(Value delegate, JimpleMethod method) {
    this(delegate, method, null);
  }

  protected JimpleVal(Value delegate, JimpleMethod method, Edge unbalanced) {
    super(method, unbalanced);

    if (delegate == null) throw new RuntimeException("Value must not be null!");
    this.delegate = delegate;
    this.method = method;
  }

  @Override
  public JimpleType getType() {
    return new JimpleType(delegate.getType(), method.getScene());
  }

  @Override
  public boolean isStatic() {
    return false;
  }

  @Override
  public boolean isNewExpr() {
    return delegate instanceof NewExpr;
  }

  @Override
  public Type getNewExprType() {
    if (isNewExpr()) {
      return new JimpleType(delegate.getType(), method.getScene());
    }

    throw new RuntimeException("Val is not a new expression");
  }

  @Override
  public Val asUnbalanced(Edge stmt) {
    return new JimpleVal(delegate, method, stmt);
  }

  @Override
  public boolean isLocal() {
    return delegate instanceof Local;
  }

  @Override
  public boolean isArrayAllocationVal() {
    return delegate instanceof NewArrayExpr || delegate instanceof NewMultiArrayExpr;
  }

  @Override
  public Val getArrayAllocationSize() {
    if (delegate instanceof NewArrayExpr) {
      NewArrayExpr newArrayExpr = (NewArrayExpr) delegate;

      return new JimpleVal(newArrayExpr.getSize(), method);
    }

    if (delegate instanceof NewMultiArrayExpr) {
      NewMultiArrayExpr expr = (NewMultiArrayExpr) delegate;

      return new JimpleVal(expr.getSize(0), method);
    }

    throw new RuntimeException("Val is not an array allocation val");
  }

  @Override
  public boolean isNull() {
    return delegate instanceof NullConstant;
  }

  @Override
  public boolean isStringConstant() {
    return delegate instanceof StringConstant;
  }

  @Override
  public String getStringValue() {
    if (isStringConstant()) {
      return ((StringConstant) delegate).value;
    }

    throw new RuntimeException("Val is not a String constant");
  }

  @Override
  public boolean isCast() {
    return delegate instanceof CastExpr;
  }

  @Override
  public Val getCastOp() {
    if (isCast()) {
      CastExpr cast = (CastExpr) delegate;
      return new JimpleVal(cast.getOp(), method);
    }

    throw new RuntimeException("Val is not a cast expression");
  }

  @Override
  public boolean isArrayRef() {
    return delegate instanceof ArrayRef;
  }

  @Override
  public IArrayRef getArrayBase() {
    if (isArrayRef()) {
      ArrayRef arrayRef = (ArrayRef) delegate;

      return new JimpleArrayRef(arrayRef, method);
    }

    throw new RuntimeException("Val is not an array ref");
  }

  @Override
  public boolean isInstanceOfExpr() {
    return delegate instanceof InstanceOfExpr;
  }

  @Override
  public Val getInstanceOfOp() {
    if (isInstanceOfExpr()) {
      InstanceOfExpr val = (InstanceOfExpr) delegate;
      return new JimpleVal(val.getOp(), method);
    }

    throw new RuntimeException("Val is not an instanceOf operator");
  }

  @Override
  public boolean isLengthExpr() {
    return delegate instanceof LengthExpr;
  }

  @Override
  public Val getLengthOp() {
    if (isLengthExpr()) {
      LengthExpr val = (LengthExpr) delegate;
      return new JimpleVal(val.getOp(), method);
    }

    throw new RuntimeException("Val is not a length expression");
  }

  @Override
  public boolean isIntConstant() {
    return delegate instanceof IntConstant;
  }

  @Override
  public int getIntValue() {
    if (isIntConstant()) {
      return ((IntConstant) delegate).value;
    }

    throw new RuntimeException("Val is not an integer constant");
  }

  @Override
  public boolean isLongConstant() {
    return delegate instanceof LongConstant;
  }

  @Override
  public long getLongValue() {
    if (isLongConstant()) {
      return ((LongConstant) delegate).value;
    }

    throw new RuntimeException("Val is not a long constant");
  }

  @Override
  public boolean isClassConstant() {
    return delegate instanceof ClassConstant;
  }

  @Override
  public Type getClassConstantType() {
    if (isClassConstant()) {
      ClassConstant constant = (ClassConstant) delegate;

      return new JimpleType(constant.toSootType(), method.getScene());
    }

    throw new RuntimeException("Val is not a class constant");
  }

  @Override
  public Val withNewMethod(Method callee) {
    throw new RuntimeException("Only allowed for static fields");
  }

  @Override
  public Val withSecondVal(Val leftOp) {
    return new JimpleDoubleVal(delegate, method, leftOp);
  }

  @Override
  public String getVariableName() {
    return delegate.toString();
  }

  public Value getDelegate() {
    return delegate;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    JimpleVal jimpleVal = (JimpleVal) o;
    return Objects.equals(delegate, jimpleVal.delegate);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), delegate);
  }

  @Override
  public String toString() {
    return delegate.toString() + (isUnbalanced() ? " unbalanced " + unbalancedStmt : "");
  }
}
