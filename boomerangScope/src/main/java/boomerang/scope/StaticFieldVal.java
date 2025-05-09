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

/**
 * Intermediate class that represents a static field reference <i>C.f</i> for a class <i>C</i> and a
 * field <i>f</i>.
 */
public abstract class StaticFieldVal extends Val implements IStaticFieldRef {

  protected StaticFieldVal(Method method, Edge unbalanced) {
    super(method, unbalanced);
  }

  @Override
  public StaticFieldVal asStaticFieldVal() {
    return this;
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
}
