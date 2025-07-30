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

import de.fraunhofer.iem.Empty;
import java.util.Collection;

public class EpsilonStatement extends Statement implements Empty {

  private static EpsilonStatement instance;

  private EpsilonStatement() {
    super(null);
  }

  public static EpsilonStatement getInstance() {
    if (instance == null) {
      instance = new EpsilonStatement();
    }

    return instance;
  }

  @Override
  public boolean containsInvokeExpr() {
    return false;
  }

  @Override
  public Field getWrittenField() {
    throw new RuntimeException("Epsilon statement is not a field write statement");
  }

  @Override
  public boolean isFieldWriteWithBase(Val base) {
    return false;
  }

  @Override
  public Field getLoadedField() {
    throw new RuntimeException("Epsilon statement is not a field load statement");
  }

  @Override
  public boolean isFieldLoadWithBase(Val base) {
    return false;
  }

  @Override
  public boolean isParameter(Val value) {
    return false;
  }

  @Override
  public boolean assignsValue(Val value) {
    return false;
  }

  @Override
  public boolean isReturnOperator(Val val) {
    return false;
  }

  @Override
  public boolean uses(Val value) {
    return false;
  }

  @Override
  public boolean isAssignStmt() {
    return false;
  }

  @Override
  public Val getLeftOp() {
    throw new RuntimeException("Epsilon statement is not an assign statement");
  }

  @Override
  public Val getRightOp() {
    throw new RuntimeException("Epsilon statement is not an assign statement");
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
  public InvokeExpr getInvokeExpr() {
    throw new RuntimeException("Epsilon statement has no invoke expression");
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
    throw new RuntimeException("Epsilon statement is not an if statement");
  }

  @Override
  public Val getReturnOp() {
    throw new RuntimeException("Epsilon statement is not a return statement");
  }

  @Override
  public boolean isFieldStore() {
    return false;
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
  public boolean killAtIfStmt(Val fact, Statement successor) {
    return false;
  }

  @Override
  public IInstanceFieldRef getFieldStore() {
    throw new RuntimeException("Epsilon statement is not a field store statement");
  }

  @Override
  public IInstanceFieldRef getFieldLoad() {
    throw new RuntimeException("Epsilon statement is not a field load statement");
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
  public IStaticFieldRef getStaticField() {
    throw new RuntimeException("Epsilon statement has no static field");
  }

  @Override
  public boolean isPhiStatement() {
    return false;
  }

  @Override
  public Collection<Val> getPhiVals() {
    throw new RuntimeException("Epsilon statement is not a phi statement");
  }

  @Override
  public IArrayRef getArrayBase() {
    throw new RuntimeException("Epsilon statement has no array base");
  }

  @Override
  public int getLineNumber() {
    return -1;
  }

  @Override
  public boolean isCatchStmt() {
    return false;
  }

  @Override
  public int hashCode() {
    return System.identityHashCode(this);
  }

  @Override
  public boolean equals(Object obj) {
    return this == obj;
  }

  @Override
  public String toString() {
    return "Eps_s";
  }
}
