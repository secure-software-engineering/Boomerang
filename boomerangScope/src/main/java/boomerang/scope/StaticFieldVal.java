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

public class StaticFieldVal extends Val implements IStaticFieldRef {

  private final WrappedClass declaringClass;
  private final Field field;

  public StaticFieldVal(WrappedClass declaringClass, Field field, Method method) {
    this(declaringClass, field, method, null);
  }

  protected StaticFieldVal(
      WrappedClass declaringClass, Field field, Method method, Edge unbalanced) {
    super(method, unbalanced);

    this.declaringClass = declaringClass;
    this.field = field;
  }

  @Override
  public WrappedClass getDeclaringClass() {
    return declaringClass;
  }

  @Override
  public Field getField() {
    return field;
  }

  public Val asUnbalanced(Edge stmt) {
    return new StaticFieldVal(declaringClass, field, m, stmt);
  }

  @Override
  public Type getType() {
    return field.getType();
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
  public boolean isLocal() {
    return false;
  }

  @Override
  public boolean isArrayAllocationVal() {
    return false;
  }

  @Override
  public Val getArrayAllocationSize() {
    throw new RuntimeException("Static field val is not an array allocation val");
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
    throw new RuntimeException("Static field val is not a String constant");
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
    throw new RuntimeException("Static field val is not a cast expression");
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
    throw new RuntimeException("Static field val is not an instance of expression");
  }

  @Override
  public boolean isLengthExpr() {
    return false;
  }

  @Override
  public Val getLengthOp() {
    throw new RuntimeException("Static field val is not a length expression");
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
    return new StaticFieldVal(declaringClass, field, callee);
  }

  @Override
  public boolean isLongConstant() {
    return false;
  }

  @Override
  public int getIntValue() {
    throw new RuntimeException("Static field val is not an int constant");
  }

  @Override
  public long getLongValue() {
    throw new RuntimeException("Static field val is not a long constant");
  }

  @Override
  public IArrayRef getArrayBase() {
    throw new RuntimeException("Static field val has no array base");
  }

  @Override
  public String getVariableName() {
    return declaringClass + "." + field.getName();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    StaticFieldVal that = (StaticFieldVal) o;
    return Objects.equals(declaringClass, that.declaringClass) && Objects.equals(field, that.field);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), declaringClass, field);
  }

  @Override
  public String toString() {
    return getVariableName();
  }
}
