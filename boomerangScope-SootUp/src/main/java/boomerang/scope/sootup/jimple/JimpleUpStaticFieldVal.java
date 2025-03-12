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
package boomerang.scope.sootup.jimple;

import boomerang.scope.ControlFlowGraph;
import boomerang.scope.Field;
import boomerang.scope.Method;
import boomerang.scope.Pair;
import boomerang.scope.StaticFieldVal;
import boomerang.scope.Type;
import boomerang.scope.Val;
import java.util.Arrays;

public class JimpleUpStaticFieldVal extends StaticFieldVal {

  private final JimpleUpField field;

  public JimpleUpStaticFieldVal(JimpleUpField field, Method method) {
    this(field, method, null);
  }

  private JimpleUpStaticFieldVal(
      JimpleUpField field, Method method, ControlFlowGraph.Edge unbalanced) {
    super(method, unbalanced);

    this.field = field;
  }

  @Override
  public Field field() {
    return field;
  }

  @Override
  public Type getType() {
    return new JimpleUpType(field.getDelegate().getType());
  }

  @Override
  public boolean isStatic() {
    return true;
  }

  @Override
  public boolean isNewExpr() {
    return false;
  }

  @Override
  public Type getNewExprType() {
    throw new RuntimeException("Static field val is not a new expression");
  }

  @Override
  public Val asUnbalanced(ControlFlowGraph.Edge stmt) {
    return new JimpleUpStaticFieldVal(field, m, stmt);
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
    throw new RuntimeException("Static field val is not a string constant");
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
    throw new RuntimeException("Static field val is not a cast operation");
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
    throw new RuntimeException("Static field val is not an instance of operation");
  }

  @Override
  public boolean isLengthExpr() {
    return false;
  }

  @Override
  public Val getLengthOp() {
    throw new RuntimeException("Static field val is not a length operation");
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
    throw new RuntimeException("Static field val is not a class constant");
  }

  @Override
  public Val withNewMethod(Method callee) {
    return new JimpleUpStaticFieldVal(field, callee);
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
    throw new RuntimeException("Static field val has not an array base");
  }

  @Override
  public String getVariableName() {
    return field.toString();
  }

  @Override
  public int hashCode() {
    return Arrays.hashCode(new Object[] {super.hashCode(), field});
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (!super.equals(obj)) return false;
    if (getClass() != obj.getClass()) return false;

    JimpleUpStaticFieldVal other = (JimpleUpStaticFieldVal) obj;
    if (field == null) {
      return other.field == null;
    } else return field.equals(other.field);
  }

  @Override
  public String toString() {
    return "StaticField: " + field;
  }
}
