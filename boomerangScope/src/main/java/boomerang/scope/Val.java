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

import java.util.Objects;

public abstract class Val {

  protected final Method m;
  protected final ControlFlowGraph.Edge unbalancedStmt;

  protected Val(Method m) {
    this.m = m;
    this.unbalancedStmt = null;
  }

  protected Val(Method m, ControlFlowGraph.Edge unbalancedStmt) {
    this.m = m;
    this.unbalancedStmt = unbalancedStmt;
  }

  protected Val() {
    this.m = null;
    this.unbalancedStmt = null;
  }

  public abstract Type getType();

  public Method m() {
    return m;
  }

  public abstract boolean isStatic();

  public abstract boolean isNewExpr();

  public abstract Type getNewExprType();

  public boolean isUnbalanced() {
    return unbalancedStmt != null;
  }

  public abstract Val asUnbalanced(ControlFlowGraph.Edge stmt);

  public abstract boolean isLocal();

  public abstract boolean isArrayAllocationVal();

  // TODO Change to list to include all dimensions (not just the first one)
  public abstract Val getArrayAllocationSize();

  public abstract boolean isNull();

  public abstract boolean isStringConstant();

  public abstract String getStringValue();

  public abstract boolean isCast();

  public abstract Val getCastOp();

  public abstract boolean isArrayRef();

  public abstract boolean isInstanceOfExpr();

  public abstract Val getInstanceOfOp();

  public abstract boolean isLengthExpr();

  public abstract Val getLengthOp();

  public abstract boolean isIntConstant();

  public abstract boolean isClassConstant();

  public abstract Type getClassConstantType();

  public abstract Val withNewMethod(Method callee);

  public Val withSecondVal(Val secondVal) {
    throw new UnsupportedOperationException("Not implemented");
  }

  public abstract boolean isLongConstant();

  public boolean isConstant() {
    return isClassConstant() || isIntConstant() || isStringConstant() || isLongConstant();
  }

  public abstract int getIntValue();

  public abstract long getLongValue();

  public abstract IArrayRef getArrayBase();

  public boolean isThisLocal() {
    return !m().isStatic() && m().getThisLocal().equals(this);
  }

  public boolean isReturnLocal() {
    return m().getReturnLocals().contains(this);
  }

  public boolean isParameterLocal(int i) {
    return i < m().getParameterLocals().size() && m().getParameterLocal(i).equals(this);
  }

  public abstract String getVariableName();

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Val val = (Val) o;
    return Objects.equals(m, val.m) && Objects.equals(unbalancedStmt, val.unbalancedStmt);
  }

  @Override
  public int hashCode() {
    return Objects.hash(m, unbalancedStmt);
  }
}
