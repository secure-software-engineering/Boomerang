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

public final class ValCollection {

  private static Val zeroVal;
  private static Val trueVal;
  private static Val falseVal;

  public static Val zero() {
    if (zeroVal == null) {
      zeroVal =
          new PredefinedVal("ZERO") {
            @Override
            public Type getType() {
              throw new RuntimeException("ZERO val has no type");
            }
          };
    }

    return zeroVal;
  }

  public static Val trueVal() {
    if (trueVal == null) {
      trueVal =
          new PredefinedVal("TRUE") {

            @Override
            public Type getType() {
              return new BooleanType(rep);
            }
          };
    }
    return trueVal;
  }

  public static Val falseVal() {
    if (falseVal == null) {
      falseVal =
          new PredefinedVal("FALSE") {
            @Override
            public Type getType() {
              return new BooleanType(rep);
            }
          };
    }

    return falseVal;
  }

  private abstract static class PredefinedVal extends Val {

    protected final String rep;

    protected PredefinedVal(String rep) {
      this.rep = rep;
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
      throw new RuntimeException("Predefined val is not a new expression");
    }

    @Override
    public Val asUnbalanced(ControlFlowGraph.Edge stmt) {
      throw new RuntimeException("Predefined val cannot be unbalanced");
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
      throw new RuntimeException("Predefined val is not an array allocation val");
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
      throw new RuntimeException("Predefined val is not a String constant");
    }

    @Override
    public boolean isCast() {
      return false;
    }

    @Override
    public Val getCastOp() {
      throw new RuntimeException("Predefined val is not a cast expression");
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
      throw new RuntimeException("Predefined val is not an instanceOf expression");
    }

    @Override
    public boolean isLengthExpr() {
      return false;
    }

    @Override
    public Val getLengthOp() {
      throw new RuntimeException("Predefined val is not a length expression");
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
      throw new RuntimeException("Predefined val is not a class constant");
    }

    @Override
    public Val withNewMethod(Method callee) {
      throw new RuntimeException("Not supported");
    }

    @Override
    public boolean isLongConstant() {
      return false;
    }

    @Override
    public int getIntValue() {
      throw new RuntimeException("Predefined val is not an int constant");
    }

    @Override
    public long getLongValue() {
      throw new RuntimeException("Predefined val is not a long constant");
    }

    @Override
    public IArrayRef getArrayBase() {
      throw new RuntimeException("Predefined val is not an array ref");
    }

    @Override
    public String getVariableName() {
      return rep;
    }

    @Override
    public String toString() {
      return rep;
    }
  }

  private static class BooleanType implements Type {

    private final String name;

    public BooleanType(String name) {
      this.name = name;
    }

    @Override
    public boolean isNullType() {
      return false;
    }

    @Override
    public boolean isRefType() {
      return false;
    }

    @Override
    public boolean isArrayType() {
      return false;
    }

    @Override
    public Type getArrayBaseType() {
      throw new RuntimeException("Boolean type is not an array type");
    }

    @Override
    public WrappedClass getWrappedClass() {
      throw new RuntimeException("Primitive type boolean has no declaring class");
    }

    @Override
    public boolean doesCastFail(Type targetVal, Val target) {
      return false;
    }

    @Override
    public boolean isSubtypeOf(String type) {
      return false;
    }

    @Override
    public boolean isSupertypeOf(String subType) {
      return false;
    }

    @Override
    public boolean isBooleanType() {
      return true;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      BooleanType that = (BooleanType) o;
      return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
      return Objects.hash(name);
    }
  }
}
