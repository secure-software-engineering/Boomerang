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
import boomerang.scope.Method;
import boomerang.scope.Pair;
import boomerang.scope.Type;
import boomerang.scope.Val;
import boomerang.scope.sootup.SootUpFrameworkScope;
import java.util.Arrays;
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
import sootup.core.types.NullType;

public class JimpleUpVal extends Val {

  private final Value delegate;

  public JimpleUpVal(Value delegate, Method method) {
    this(delegate, method, null);
  }

  protected JimpleUpVal(Value delegate, Method method, ControlFlowGraph.Edge unbalanced) {
    super(method, unbalanced);

    if (delegate == null) {
      throw new RuntimeException("Value must not be null");
    }
    this.delegate = delegate;
  }

  @Override
  public Type getType() {
    if (delegate == null) {
      return new JimpleUpType(NullType.getInstance());
    } else {
      return new JimpleUpType(delegate.getType());
    }
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
    assert isNewExpr();
    return new JimpleUpType(((JNewExpr) delegate).getType());
  }

  @Override
  public Val asUnbalanced(ControlFlowGraph.Edge stmt) {
    return new JimpleUpVal(delegate, m, stmt);
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

      return new JimpleUpVal(newArrayExpr.getSize(), m);
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
    assert isStringConstant();
    return ((StringConstant) delegate).getValue();
  }

  @Override
  public boolean isStringBufferOrBuilder() {
    Type type = getType();
    return type.toString().equals("java.lang.String")
        || type.toString().equals("java.lang.StringBuilder")
        || type.toString().equals("java.lang.StringBuffer");
  }

  @Override
  public boolean isThrowableAllocationType() {
    return SootUpFrameworkScope.getInstance()
        .getView()
        .getTypeHierarchy()
        .isSubtype(
            ((JimpleUpType) getType()).getDelegate(),
            SootUpFrameworkScope.getInstance()
                .getIdentifierFactory()
                .getClassType("java.lang.Throwable"));
  }

  @Override
  public boolean isCast() {
    return delegate instanceof JCastExpr;
  }

  @Override
  public Val getCastOp() {
    assert isCast();

    JCastExpr castExpr = (JCastExpr) delegate;
    return new JimpleUpVal(castExpr.getOp(), m);
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
    assert isInstanceOfExpr();

    JInstanceOfExpr instanceOfExpr = (JInstanceOfExpr) delegate;
    return new JimpleUpVal(instanceOfExpr.getOp(), m);
  }

  @Override
  public boolean isLengthExpr() {
    return delegate instanceof JLengthExpr;
  }

  @Override
  public Val getLengthOp() {
    assert isLengthExpr();

    JLengthExpr lengthExpr = (JLengthExpr) delegate;
    return new JimpleUpVal(lengthExpr.getOp(), m);
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
    assert isClassConstant();

    ClassConstant constant = (ClassConstant) delegate;
    return new JimpleUpType(constant.getType());
  }

  @Override
  public Val withNewMethod(Method callee) {
    throw new RuntimeException("Only allowed for static fields");
  }

  @Override
  public Val withSecondVal(Val leftOp) {
    return new JimpleUpDoubleVal(delegate, m, leftOp);
  }

  @Override
  public boolean isLongConstant() {
    return delegate instanceof LongConstant;
  }

  @Override
  public int getIntValue() {
    assert isIntConstant();

    IntConstant intConstant = (IntConstant) delegate;
    return intConstant.getValue();
  }

  @Override
  public long getLongValue() {
    assert isLongConstant();

    LongConstant longConstant = (LongConstant) delegate;
    return longConstant.getValue();
  }

  @Override
  public Pair<Val, Integer> getArrayBase() {
    assert isArrayRef();

    JArrayRef arrayRef = (JArrayRef) delegate;
    return new Pair<>(
        new JimpleUpVal(arrayRef.getBase(), m),
        arrayRef.getIndex() instanceof IntConstant
            ? ((IntConstant) arrayRef.getIndex()).getValue()
            : -1);
  }

  @Override
  public String getVariableName() {
    return delegate.toString();
  }

  public Value getDelegate() {
    return delegate;
  }

  @Override
  public int hashCode() {
    return Arrays.hashCode(new Object[] {super.hashCode(), delegate});
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (!super.equals(obj)) return false;
    if (getClass() != obj.getClass()) return false;

    JimpleUpVal other = (JimpleUpVal) obj;
    if (delegate == null) {
      return other.delegate == null;
    } else return delegate.equals(other.delegate);
  }

  @Override
  public String toString() {
    return delegate.toString()
        + " ("
        + m.getDeclaringClass()
        + "."
        + m
        + ")"
        + (isUnbalanced() ? " unbalanced " + unbalancedStmt : "");
  }
}
