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
import boomerang.scope.Method;
import boomerang.scope.Pair;
import boomerang.scope.StaticFieldVal;
import boomerang.scope.Type;
import boomerang.scope.Val;
import java.util.Objects;
import soot.Local;
import soot.NullType;
import soot.Scene;
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
import soot.jimple.StaticFieldRef;
import soot.jimple.StringConstant;

public class JimpleVal extends Val {

  private final Value v;

  public JimpleVal(Value v, Method m) {
    this(v, m, null);
  }

  protected JimpleVal(Value v, Method m, Edge unbalanced) {
    super(m, unbalanced);
    if (v == null) throw new RuntimeException("Value must not be null!");
    this.v = v;
  }

  @Override
  public JimpleType getType() {
    return v == null ? new JimpleType(NullType.v()) : new JimpleType(v.getType());
  }

  @Override
  public boolean isStatic() {
    return false;
  }

  @Override
  public boolean isNewExpr() {
    return v instanceof NewExpr;
  }

  @Override
  public Type getNewExprType() {
    if (isNewExpr()) {
      return new JimpleType(v.getType());
    }

    throw new RuntimeException("Val is not a new expression");
  }

  @Override
  public Val asUnbalanced(Edge stmt) {
    return new JimpleVal(v, m, stmt);
  }

  @Override
  public boolean isLocal() {
    return v instanceof Local;
  }

  @Override
  public boolean isArrayAllocationVal() {
    // TODO Split this into single array and multi array allocation
    return v instanceof NewArrayExpr || v instanceof NewMultiArrayExpr;
  }

  @Override
  public Val getArrayAllocationSize() {
    // TODO Split this into single array and multi array allocation
    if (v instanceof NewArrayExpr) {
      NewArrayExpr newArrayExpr = (NewArrayExpr) v;

      return new JimpleVal(newArrayExpr.getSize(), m);
    }

    if (v instanceof NewMultiArrayExpr) {
      NewMultiArrayExpr expr = (NewMultiArrayExpr) v;

      return new JimpleVal(expr.getSize(0), m);
    }

    throw new RuntimeException("Val is not an array allocation val");
  }

  @Override
  public boolean isNull() {
    return v instanceof NullConstant;
  }

  @Override
  public boolean isStringConstant() {
    return v instanceof StringConstant;
  }

  @Override
  public String getStringValue() {
    if (isStringConstant()) {
      return ((StringConstant) v).value;
    }

    throw new RuntimeException("Val is not a String constant");
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
    return Scene.v()
        .getOrMakeFastHierarchy()
        .canStoreType(getType().getDelegate(), Scene.v().getType("java.lang.Throwable"));
  }

  @Override
  public boolean isCast() {
    return v instanceof CastExpr;
  }

  @Override
  public Val getCastOp() {
    if (isCast()) {
      CastExpr cast = (CastExpr) v;
      return new JimpleVal(cast.getOp(), m);
    }

    throw new RuntimeException("Val is not a cast expression");
  }

  public boolean isInstanceFieldRef() {
    return v instanceof soot.jimple.InstanceFieldRef;
  }

  public boolean isStaticFieldRef() {
    return v instanceof StaticFieldRef;
  }

  public StaticFieldVal getStaticField() {
    StaticFieldRef val = (StaticFieldRef) v;
    return new JimpleStaticFieldVal(new JimpleField(val.getField()), m);
  }

  @Override
  public boolean isArrayRef() {
    return v instanceof ArrayRef;
  }

  @Override
  public Pair<Val, Integer> getArrayBase() {
    if (isArrayRef()) {
      return new Pair<>(
          new JimpleVal(((ArrayRef) v).getBase(), m),
          ((ArrayRef) v).getIndex() instanceof IntConstant
              ? ((IntConstant) ((ArrayRef) v).getIndex()).value
              : -1);
    }

    throw new RuntimeException("Val is not an array ref");
  }

  @Override
  public boolean isInstanceOfExpr() {
    return v instanceof InstanceOfExpr;
  }

  @Override
  public Val getInstanceOfOp() {
    if (isInstanceOfExpr()) {
      InstanceOfExpr val = (InstanceOfExpr) v;
      return new JimpleVal(val.getOp(), m);
    }

    throw new RuntimeException("Val is not an instanceOf operator");
  }

  @Override
  public boolean isLengthExpr() {
    return v instanceof LengthExpr;
  }

  @Override
  public Val getLengthOp() {
    if (isLengthExpr()) {
      LengthExpr val = (LengthExpr) v;
      return new JimpleVal(val.getOp(), m);
    }

    throw new RuntimeException("Val is not a length expression");
  }

  @Override
  public boolean isIntConstant() {
    return v instanceof IntConstant;
  }

  @Override
  public int getIntValue() {
    if (isIntConstant()) {
      return ((IntConstant) v).value;
    }

    throw new RuntimeException("Val is not an integer constant");
  }

  @Override
  public boolean isLongConstant() {
    return v instanceof LongConstant;
  }

  @Override
  public long getLongValue() {
    if (isLongConstant()) {
      return ((LongConstant) v).value;
    }

    throw new RuntimeException("Val is not a long constant");
  }

  @Override
  public boolean isClassConstant() {
    return v instanceof ClassConstant;
  }

  @Override
  public Type getClassConstantType() {
    if (isClassConstant()) {
      return new JimpleType(((ClassConstant) v).toSootType());
    }

    throw new RuntimeException("Val is not a class constant");
  }

  @Override
  public Val withNewMethod(Method callee) {
    throw new RuntimeException("Only allowed for static fields");
  }

  @Override
  public Val withSecondVal(Val leftOp) {
    return new JimpleDoubleVal(v, m, leftOp);
  }

  @Override
  public String getVariableName() {
    return v.toString();
  }

  public Value getDelegate() {
    return v;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    JimpleVal jimpleVal = (JimpleVal) o;
    return Objects.equals(v, jimpleVal.v);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), v);
  }

  @Override
  public String toString() {
    return v.toString() + " (" + m + ")" + (isUnbalanced() ? " unbalanced " + unbalancedStmt : "");
  }
}
