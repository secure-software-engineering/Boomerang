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
package boomerang.scope.wala;

import boomerang.scope.Field;
import boomerang.scope.IArrayRef;
import boomerang.scope.IInstanceFieldRef;
import boomerang.scope.IfStatement;
import boomerang.scope.InvokeExpr;
import boomerang.scope.Statement;
import boomerang.scope.StaticFieldVal;
import boomerang.scope.Val;
import java.util.Collection;

public class WALAUnitializedFieldStatement extends WALAStatement {

  private final WALAField field;
  private final WALAMethod method;
  private final Val thisLocal;
  private final Val rightOp;

  public WALAUnitializedFieldStatement(
      WALAField field, WALAMethod method, Val thisLocal, Val rightOp) {
    super("this." + field + " = " + rightOp, method);
    this.field = field;
    this.method = method;
    this.thisLocal = thisLocal;
    this.rightOp = rightOp;
  }

  @Override
  public boolean containsInvokeExpr() {
    return false;
  }

  @Override
  public Field getWrittenField() {
    return field;
  }

  @Override
  public Val getRightOp() {
    return rightOp;
  }

  @Override
  public boolean isFieldWriteWithBase(Val base) {
    return thisLocal.equals(base);
  }

  @Override
  public Field getLoadedField() {
    throw new RuntimeException("Illegal");
  }

  @Override
  public boolean isFieldLoadWithBase(Val base) {
    return false;
  }

  @Override
  public boolean isAssignStmt() {
    return true;
  }

  @Override
  public boolean isInstanceOfStatement(Val fact) {
    return false;
  }

  @Override
  public boolean isCast() {
    return false;
  }

  @Override
  public boolean isPhiStatement() {
    return false;
  }

  @Override
  public InvokeExpr getInvokeExpr() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public boolean isReturnStmt() {
    return false;
  }

  @Override
  public boolean isThrowStmt() {
    return false;
  }

  @Override
  public boolean isIfStmt() {
    return false;
  }

  @Override
  public IfStatement getIfStmt() {
    throw new RuntimeException("Illegal");
  }

  @Override
  public Val getReturnOp() {
    throw new RuntimeException("Illegal");
  }

  @Override
  public boolean isFieldStore() {
    return true;
  }

  @Override
  public boolean isArrayStore() {
    return false;
  }

  @Override
  public boolean isArrayLoad() {
    return false;
  }

  @Override
  public boolean isFieldLoad() {
    return false;
  }

  @Override
  public boolean isIdentityStmt() {
    return false;
  }

  @Override
  public IInstanceFieldRef getFieldStore() {
    // return new Pair<Val, Field>(thisLocal, field);
    throw new UnsupportedOperationException("Not implemented yet");
  }

  @Override
  public IInstanceFieldRef getFieldLoad() {
    throw new RuntimeException("Illegal");
  }

  @Override
  public boolean isStaticFieldLoad() {
    return false;
  }

  @Override
  public boolean isStaticFieldStore() {
    return false;
  }

  @Override
  public StaticFieldVal getStaticField() {
    throw new RuntimeException("Illegal");
  }

  @Override
  public boolean killAtIfStmt(Val fact, Statement successor) {
    return false;
  }

  @Override
  public Collection<Val> getPhiVals() {
    return null;
  }

  @Override
  public IArrayRef getArrayBase() {
    throw new RuntimeException("Illegal");
  }

  @Override
  public int getLineNumber() {
    return 0;
  }

  @Override
  public boolean isCatchStmt() {
    return false;
  }
}
