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

public abstract class InstanceFieldVal extends Val implements IInstanceFieldRef {

  protected InstanceFieldVal(Method method, ControlFlowGraph.Edge unbalanced) {
    super(method, unbalanced);
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
    throw new RuntimeException("Instance field is not a new expression");
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
    throw new RuntimeException("Instance field is not an array allocation val");
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
    throw new RuntimeException("Instance field is not a String constant");
  }

  @Override
  public boolean isCast() {
    return false;
  }

  @Override
  public Val getCastOp() {
    throw new RuntimeException("Instance field is not a cast expresssion");
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
    throw new RuntimeException("Instance field is not an instanceOf expression");
  }

  @Override
  public boolean isLengthExpr() {
    return false;
  }

  @Override
  public Val getLengthOp() {
    throw new RuntimeException("Instance field is not a length expression");
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
    throw new RuntimeException("Instance field is not a class constant");
  }

  @Override
  public boolean isLongConstant() {
    return false;
  }

  @Override
  public int getIntValue() {
    throw new RuntimeException("Instance field is not an int constant");
  }

  @Override
  public long getLongValue() {
    throw new RuntimeException("Instance field is not a long constant");
  }

  @Override
  public IArrayRef getArrayBase() {
    throw new RuntimeException("Instance field is not an array ref");
  }
}
