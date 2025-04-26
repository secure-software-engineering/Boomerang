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
import de.fraunhofer.iem.Location;
import java.util.Collection;
import java.util.Objects;

public abstract class Statement implements Location {

  private static Statement epsilon;
  private final String rep;
  protected final Method method;

  protected Statement(Method method) {
    this.rep = null;
    this.method = method;
  }

  private Statement(String rep) {
    this.rep = rep;
    this.method = null;
  }

  public static Statement epsilon() {
    if (epsilon == null) {
      epsilon = new EpsStatement();
    }
    return epsilon;
  }

  private static class EpsStatement extends Statement implements Empty {

    public EpsStatement() {
      super("Eps_s");
    }

    @Override
    public Method getMethod() {
      return null;
    }

    @Override
    public boolean containsStaticFieldAccess() {
      return false;
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
    public boolean isMultiArrayAllocation() {
      return false;
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
    public StaticFieldVal getStaticField() {
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
    public int getStartLineNumber() {
      return -1;
    }

    @Override
    public int getStartColumnNumber() {
      return -1;
    }

    @Override
    public int getEndColumnNumber() {
      return -1;
    }

    @Override
    public int getEndLineNumber() {
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
      return obj == this;
    }
  }

  public Method getMethod() {
    return this.method;
  }

  public abstract boolean containsStaticFieldAccess();

  public abstract boolean containsInvokeExpr();

  public abstract Field getWrittenField();

  public abstract boolean isFieldWriteWithBase(Val base);

  public abstract Field getLoadedField();

  public abstract boolean isFieldLoadWithBase(Val base);

  public boolean isParameter(Val value) {
    if (containsInvokeExpr()) {
      InvokeExpr invokeExpr = getInvokeExpr();
      if (invokeExpr.isInstanceInvokeExpr()) {
        if (invokeExpr.getBase().equals(value)) return true;
      }
      for (Val arg : invokeExpr.getArgs()) {
        if (arg.equals(value)) {
          return true;
        }
      }
    }
    return false;
  }

  public int getParameter(Val value) {
    if (containsInvokeExpr()) {
      InvokeExpr invokeExpr = getInvokeExpr();
      if (invokeExpr.isInstanceInvokeExpr()) {
        if (invokeExpr.getBase().equals(value)) return -2;
      }
      int index = 0;
      for (Val arg : invokeExpr.getArgs()) {
        if (arg.equals(value)) {
          return index;
        }
        index++;
      }
    }
    return -1;
  }

  public boolean isReturnOperator(Val val) {
    if (isReturnStmt()) {
      return getReturnOp().equals(val);
    }
    return false;
  }

  public boolean uses(Val value) {
    if (value.isStatic()) return true;
    if (assignsValue(value)) return true;
    if (isFieldStore()) {
      if (getFieldStore().getBase().equals(value)) return true;
    }
    if (isReturnOperator(value)) return true;
    return isParameter(value);
  }

  public boolean assignsValue(Val value) {
    if (isAssignStmt()) {
      return getLeftOp().equals(value);
    }
    return false;
  }

  public abstract boolean isAssignStmt();

  public abstract Val getLeftOp();

  public abstract Val getRightOp();

  public abstract boolean isInstanceOfStatement(Val fact);

  public abstract boolean isCast();

  public abstract boolean isPhiStatement();

  public abstract InvokeExpr getInvokeExpr();

  public abstract boolean isReturnStmt();

  public abstract boolean isThrowStmt();

  public abstract boolean isIfStmt();

  public abstract IfStatement getIfStmt();

  public abstract Val getReturnOp();

  public abstract boolean isMultiArrayAllocation();

  public abstract boolean isFieldStore();

  public abstract boolean isArrayStore();

  public abstract boolean isArrayLoad();

  public abstract boolean isFieldLoad();

  public abstract boolean isIdentityStmt();

  public abstract IInstanceFieldRef getFieldStore();

  public abstract IInstanceFieldRef getFieldLoad();

  public abstract boolean isStaticFieldLoad();

  public abstract boolean isStaticFieldStore();

  public abstract StaticFieldVal getStaticField();

  /**
   * This method kills a data-flow at an if-stmt, it is assumed that the propagated "allocation"
   * site is x = null and fact is the propagated aliased variable. (i.e., y after a statement y =
   * x). If the if-stmt checks for if y != null or if y == null, data-flow propagation can be killed
   * when along the true/false branch.
   *
   * @param fact The data-flow value that bypasses the if-stmt
   * @return true if the Val fact shall be killed
   */
  public abstract boolean killAtIfStmt(Val fact, Statement successor);

  public abstract Collection<Val> getPhiVals();

  public abstract IArrayRef getArrayBase();

  public abstract int getStartLineNumber();

  public abstract int getStartColumnNumber();

  public abstract int getEndLineNumber();

  public abstract int getEndColumnNumber();

  public abstract boolean isCatchStmt();

  @Override
  public boolean accepts(Location other) {
    return this.equals(other);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Statement statement = (Statement) o;
    return Objects.equals(rep, statement.rep) && Objects.equals(method, statement.method);
  }

  @Override
  public int hashCode() {
    return Objects.hash(rep, method);
  }

  @Override
  public String toString() {
    return rep;
  }
}
