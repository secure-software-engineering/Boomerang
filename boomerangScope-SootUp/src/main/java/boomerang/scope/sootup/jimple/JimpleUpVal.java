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
import boomerang.scope.IArrayRef;
import boomerang.scope.Method;
import boomerang.scope.Type;
import boomerang.scope.Val;
import java.util.Objects;
import sootup.core.jimple.basic.Local;
import sootup.core.jimple.basic.Value;
import sootup.core.jimple.common.constant.ClassConstant;
import sootup.core.jimple.common.constant.IntConstant;
import sootup.core.jimple.common.constant.LongConstant;
import sootup.core.jimple.common.constant.NullConstant;
import sootup.core.jimple.common.constant.StringConstant;
import sootup.core.jimple.common.expr.JCastExpr;
import sootup.core.jimple.common.expr.JInstanceOfExpr;
import sootup.core.jimple.common.expr.JLengthExpr;
import sootup.core.jimple.common.expr.JNewArrayExpr;
import sootup.core.jimple.common.expr.JNewExpr;
import sootup.core.jimple.common.expr.JNewMultiArrayExpr;
import sootup.core.jimple.common.ref.JArrayRef;

public class JimpleUpVal extends Val {

  private final JimpleUpMethod method;
  private final Value delegate;

  public JimpleUpVal(Value delegate, JimpleUpMethod method) {
    this(delegate, method, null);
  }

  protected JimpleUpVal(Value delegate, JimpleUpMethod method, ControlFlowGraph.Edge unbalanced) {
    super(method, unbalanced);

    this.delegate = delegate;
    this.method = method;

    if (delegate == null) {
      throw new RuntimeException("Value must not be null");
    }
  }

  public Value getDelegate() {
    return delegate;
  }

  @Override
  public Type getType() {
    return new JimpleUpType(delegate.getType(), method.getView());
  }

  @Override
  public boolean isStatic() {
    return false;
  }

  @Override
  public boolean isNewExpr() {
    return delegate instanceof JNewExpr;
  }

  @Override
  public Type getNewExprType() {
    if (isNewExpr()) {
      JNewExpr newExpr = (JNewExpr) delegate;

      return new JimpleUpType(newExpr.getType(), method.getView());
    }

    throw new RuntimeException("Val is not a new expression");
  }

  @Override
  public Val asUnbalanced(ControlFlowGraph.Edge stmt) {
    return new JimpleUpVal(delegate, method, stmt);
  }

  @Override
  public boolean isLocal() {
    return delegate instanceof Local;
  }

  @Override
  public boolean isArrayAllocationVal() {
    return delegate instanceof JNewArrayExpr || delegate instanceof JNewMultiArrayExpr;
  }

  @Override
  public Val getArrayAllocationSize() {
    if (delegate instanceof JNewArrayExpr) {
      JNewArrayExpr newArrayExpr = (JNewArrayExpr) delegate;

      return new JimpleUpVal(newArrayExpr.getSize(), method);
    }

    if (delegate instanceof JNewMultiArrayExpr) {
      JNewMultiArrayExpr newMultiArrayExpr = (JNewMultiArrayExpr) delegate;

      return new JimpleUpVal(newMultiArrayExpr.getSize(0), method);
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
      return ((StringConstant) delegate).getValue();
    }

    throw new RuntimeException("Val is not a String constant");
  }

  @Override
  public boolean isCast() {
    return delegate instanceof JCastExpr;
  }

  @Override
  public Val getCastOp() {
    if (isCast()) {
      JCastExpr castExpr = (JCastExpr) delegate;
      return new JimpleUpVal(castExpr.getOp(), method);
    }

    throw new RuntimeException("Val is not a cast expression");
  }

  @Override
  public boolean isArrayRef() {
    return delegate instanceof JArrayRef;
  }

  @Override
  public boolean isInstanceOfExpr() {
    return delegate instanceof JInstanceOfExpr;
  }

  @Override
  public Val getInstanceOfOp() {
    if (isInstanceOfExpr()) {
      JInstanceOfExpr instanceOfExpr = (JInstanceOfExpr) delegate;
      return new JimpleUpVal(instanceOfExpr.getOp(), method);
    }

    throw new RuntimeException("Val is not an instanceOf expression");
  }

  @Override
  public boolean isLengthExpr() {
    return delegate instanceof JLengthExpr;
  }

  @Override
  public Val getLengthOp() {
    if (isLengthExpr()) {
      JLengthExpr lengthExpr = (JLengthExpr) delegate;
      return new JimpleUpVal(lengthExpr.getOp(), method);
    }

    throw new RuntimeException("Val is not a length expression");
  }

  @Override
  public boolean isIntConstant() {
    return delegate instanceof IntConstant;
  }

  @Override
  public boolean isClassConstant() {
    return delegate instanceof ClassConstant;
  }

  @Override
  public Type getClassConstantType() {
    if (isClassConstant()) {
      ClassConstant constant = (ClassConstant) delegate;
      return new JimpleUpType(constant.getType(), method.getView());
    }

    throw new RuntimeException("Val is not a class constant");
  }

  @Override
  public Val withNewMethod(Method callee) {
    throw new RuntimeException("Only allowed for static fields");
  }

  @Override
  public Val withSecondVal(Val leftOp) {
    return new JimpleUpDoubleVal(delegate, method, leftOp);
  }

  @Override
  public boolean isLongConstant() {
    return delegate instanceof LongConstant;
  }

  @Override
  public int getIntValue() {
    if (isIntConstant()) {
      IntConstant intConstant = (IntConstant) delegate;
      return intConstant.getValue();
    }

    throw new RuntimeException("Val is not an int constant");
  }

  @Override
  public long getLongValue() {
    if (isLongConstant()) {
      LongConstant longConstant = (LongConstant) delegate;
      return longConstant.getValue();
    }

    throw new RuntimeException("Val is not a long constant");
  }

  @Override
  public IArrayRef getArrayBase() {
    if (isArrayRef()) {
      JArrayRef arrayRef = (JArrayRef) delegate;

      return new JimpleUpArrayRef(arrayRef, method);
    }

    throw new RuntimeException("Val is not an array ref");
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
    JimpleUpVal that = (JimpleUpVal) o;
    return Objects.equals(delegate, that.delegate);
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
