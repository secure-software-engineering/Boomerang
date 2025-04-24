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
package boomerang.scope;

import boomerang.scope.ControlFlowGraph.Edge;
import java.util.Objects;

public class AllocVal extends Val {

  private final Val delegate;
  private final Statement allocStatement;
  private final Val allocationVal;

  public AllocVal(Val delegate, Statement allocStatement, Val allocationVal) {
    this.delegate = delegate;
    this.allocStatement = allocStatement;
    this.allocationVal = allocationVal;
  }

  public Val getDelegate() {
    return delegate;
  }

  public Statement getAllocStatement() {
    return allocStatement;
  }

  public Val getAllocVal() {
    return allocationVal;
  }

  @Override
  public Type getType() {
    return delegate.getType();
  }

  @Override
  public Method m() {
    return delegate.m();
  }

  @Override
  public boolean isStatic() {
    return delegate.isStatic();
  }

  @Override
  public boolean isNewExpr() {
    return delegate.isNewExpr();
  }

  @Override
  public Type getNewExprType() {
    return delegate.getNewExprType();
  }

  @Override
  public boolean isUnbalanced() {
    return delegate.isUnbalanced();
  }

  @Override
  public Val asUnbalanced(Edge stmt) {
    return delegate.asUnbalanced(stmt);
  }

  @Override
  public boolean isLocal() {
    return delegate.isLocal();
  }

  @Override
  public boolean isArrayAllocationVal() {
    return delegate.isArrayAllocationVal();
  }

  @Override
  public Val getArrayAllocationSize() {
    return delegate.getArrayAllocationSize();
  }

  @Override
  public boolean isNull() {
    return allocationVal.isNull();
  }

  @Override
  public boolean isStringConstant() {
    return delegate.isStringConstant();
  }

  @Override
  public String getStringValue() {
    return delegate.getStringValue();
  }

  @Override
  public boolean isStringBufferOrBuilder() {
    return delegate.isStringBufferOrBuilder();
  }

  @Override
  public boolean isThrowableAllocationType() {
    return delegate.isThrowableAllocationType();
  }

  @Override
  public boolean isCast() {
    return delegate.isCast();
  }

  @Override
  public Val getCastOp() {
    return delegate.getCastOp();
  }

  @Override
  public boolean isArrayRef() {
    return delegate.isArrayRef();
  }

  @Override
  public boolean isInstanceOfExpr() {
    return delegate.isInstanceOfExpr();
  }

  @Override
  public Val getInstanceOfOp() {
    return delegate.getInstanceOfOp();
  }

  @Override
  public boolean isLengthExpr() {
    return delegate.isLengthExpr();
  }

  @Override
  public Val getLengthOp() {
    return delegate.getLengthOp();
  }

  @Override
  public boolean isIntConstant() {
    return delegate.isIntConstant();
  }

  @Override
  public boolean isClassConstant() {
    return delegate.isClassConstant();
  }

  @Override
  public Type getClassConstantType() {
    return delegate.getClassConstantType();
  }

  @Override
  public Val withNewMethod(Method callee) {
    return delegate.withNewMethod(callee);
  }

  @Override
  public Val withSecondVal(Val leftOp) {
    return delegate.withSecondVal(leftOp);
  }

  @Override
  public boolean isLongConstant() {
    return false;
  }

  @Override
  public int getIntValue() {
    return delegate.getIntValue();
  }

  @Override
  public long getLongValue() {
    return delegate.getLongValue();
  }

  @Override
  public Pair<Val, Integer> getArrayBase() {
    return delegate.getArrayBase();
  }

  @Override
  public String getVariableName() {
    return delegate.getVariableName();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    AllocVal allocVal = (AllocVal) o;
    return Objects.equals(delegate, allocVal.getDelegate())
        && Objects.equals(allocStatement, allocVal.getAllocStatement())
        && Objects.equals(allocationVal, allocVal.getAllocVal());
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), delegate, allocStatement, allocationVal);
  }

  @Override
  public String toString() {
    return "AllocVal{"
        + "delegate: "
        + delegate
        + ", allocStatement: "
        + allocStatement
        + ", allocationVal: "
        + allocationVal
        + "}";
  }
}
