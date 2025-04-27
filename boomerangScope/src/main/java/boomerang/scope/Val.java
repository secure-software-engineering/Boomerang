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
  private final String rep;
  protected final ControlFlowGraph.Edge unbalancedStmt;

  private static Val zeroInstance;

  protected Val(Method m) {
    this.rep = null;
    this.m = m;
    this.unbalancedStmt = null;
  }

  protected Val(Method m, ControlFlowGraph.Edge unbalancedStmt) {
    this.rep = null;
    this.m = m;
    this.unbalancedStmt = unbalancedStmt;
  }

  private Val(String rep) {
    this.rep = rep;
    this.m = null;
    this.unbalancedStmt = null;
  }

  protected Val() {
    this.rep = null;
    this.m = null;
    this.unbalancedStmt = null;
  }

  public abstract Type getType();

  public Method m() {
    return m;
  }

  @Override
  public String toString() {
    return rep;
  }

  public static Val zero() {
    if (zeroInstance == null)
      zeroInstance =
          new Val("ZERO") {

            @Override
            public Type getType() {
              throw new RuntimeException("ZERO Val has no type");
            }

            @Override
            public boolean isStatic() {
              return false;
            }

            @Override
            public boolean isNewExpr() {
              return false;
            }

            @Override
            public Type getNewExprType() {
              throw new RuntimeException("ZERO Val is not a new expression");
            }

            @Override
            public Val asUnbalanced(ControlFlowGraph.Edge stmt) {
              return null;
            }

            @Override
            public boolean isLocal() {
              return false;
            }

            @Override
            public boolean isArrayAllocationVal() {
              return false;
            }

            @Override
            public Val getArrayAllocationSize() {
              throw new RuntimeException("ZERO Val is not an array allocation val");
            }

            @Override
            public boolean isNull() {
              return false;
            }

            @Override
            public boolean isStringConstant() {
              return false;
            }

            @Override
            public String getStringValue() {
              throw new RuntimeException("ZERO Val is not a String constant");
            }

            @Override
            public boolean isCast() {
              return false;
            }

            @Override
            public Val getCastOp() {
              throw new RuntimeException("ZERO Val is not a cast expression");
            }

            @Override
            public boolean isArrayRef() {
              return false;
            }

            @Override
            public boolean isInstanceOfExpr() {
              return false;
            }

            @Override
            public Val getInstanceOfOp() {
              throw new RuntimeException("ZERO Val is not an instanceOf expression");
            }

            @Override
            public boolean isLengthExpr() {
              return false;
            }

            @Override
            public Val getLengthOp() {
              throw new RuntimeException("ZERO Val is not a length expression");
            }

            @Override
            public boolean isIntConstant() {
              return false;
            }

            @Override
            public boolean isClassConstant() {
              return false;
            }

            @Override
            public Type getClassConstantType() {
              throw new RuntimeException("ZERO Val is not a class constant");
            }

            @Override
            public Val withNewMethod(Method callee) {
              return null;
            }

            @Override
            public Val withSecondVal(Val secondVal) {
              return null;
            }

            @Override
            public boolean isLongConstant() {
              return false;
            }

            @Override
            public int getIntValue() {
              throw new RuntimeException("ZERO Val is not an int constant");
            }

            @Override
            public long getLongValue() {
              throw new RuntimeException("ZERO Val is not a long constant");
            }

            @Override
            public IArrayRef getArrayBase() {
              throw new RuntimeException("ZERO Val has no array base");
            }

            @Override
            public String getVariableName() {
              return toString();
            }
          };
    return zeroInstance;
  }

  public abstract boolean isStatic();

  public abstract boolean isNewExpr();

  public abstract Type getNewExprType();

  public boolean isUnbalanced() {
    return unbalancedStmt != null && rep == null;
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
    return Objects.equals(m, val.m)
        && Objects.equals(rep, val.rep)
        && Objects.equals(unbalancedStmt, val.unbalancedStmt);
  }

  @Override
  public int hashCode() {
    return Objects.hash(m, rep, unbalancedStmt);
  }
}
