/**
 * ***************************************************************************** 
 * Copyright (c) 2025 Fraunhofer IEM, Paderborn, Germany. This program and the
 * accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at http://www.eclipse.org/legal/epl-2.0.
 *
 * <p>SPDX-License-Identifier: EPL-2.0
 *
 * <p>Contributors: Johannes Spaeth - initial API and implementation
 * *****************************************************************************
 */
package boomerang.scope.soot.jimple;

import boomerang.scope.ControlFlowGraph;
import boomerang.scope.Field;
import boomerang.scope.Method;
import boomerang.scope.Pair;
import boomerang.scope.StaticFieldVal;
import boomerang.scope.Type;
import boomerang.scope.Val;
import java.util.Objects;

public class JimpleStaticFieldVal extends StaticFieldVal {

  private final JimpleField field;

  public JimpleStaticFieldVal(JimpleField field, Method m) {
    this(field, m, null);
  }

  private JimpleStaticFieldVal(JimpleField field, Method m, ControlFlowGraph.Edge unbalanced) {
    super(m, unbalanced);
    this.field = field;
  }

  @Override
  public boolean isStatic() {
    return true;
  }

  public Field field() {
    return field;
  }

  @Override
  public Val asUnbalanced(ControlFlowGraph.Edge stmt) {
    return new JimpleStaticFieldVal(field, m, stmt);
  }

  @Override
  public Type getType() {
    return new JimpleType(field.getDelegate().getType());
  }

  @Override
  public boolean isNewExpr() {
    return false;
  }

  @Override
  public Type getNewExprType() {
    throw new RuntimeException("Fault!");
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
    throw new RuntimeException("Static Val is not an array allocation val");
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
    throw new RuntimeException("Fault!");
  }

  @Override
  public boolean isStringBufferOrBuilder() {
    return false;
  }

  @Override
  public boolean isThrowableAllocationType() {
    return false;
  }

  @Override
  public boolean isCast() {
    return false;
  }

  @Override
  public Val getCastOp() {
    throw new RuntimeException("Fault!");
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
    throw new RuntimeException("Fault!");
  }

  @Override
  public boolean isLengthExpr() {
    return false;
  }

  @Override
  public Val getLengthOp() {
    throw new RuntimeException("Fault!");
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
    throw new RuntimeException("Fault!");
  }

  @Override
  public Val withNewMethod(Method callee) {
    return new JimpleStaticFieldVal(field, callee);
  }

  @Override
  public boolean isLongConstant() {
    return false;
  }

  @Override
  public int getIntValue() {
    return -1;
  }

  @Override
  public long getLongValue() {
    return -1;
  }

  @Override
  public Pair<Val, Integer> getArrayBase() {
    return null;
  }

  @Override
  public String getVariableName() {
    return toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    JimpleStaticFieldVal that = (JimpleStaticFieldVal) o;
    return Objects.equals(field, that.field);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), field);
  }

  @Override
  public String toString() {
    return "StaticField: " + field;
  }
}
