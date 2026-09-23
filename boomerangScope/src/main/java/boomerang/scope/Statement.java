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

import de.fraunhofer.iem.Location;
import java.util.Collection;
import java.util.Objects;

public abstract class Statement implements Location {

  protected final Method method;
  private final int baseHashCode;

  protected Statement(Method method) {
    this.method = method;
    this.baseHashCode = Objects.hash(method);
  }

  public Method getMethod() {
    return method;
  }

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

  public Collection<Statement> getPredecessors() {
    if (method == null) {
      throw new RuntimeException("Statement has no correct method");
    }

    return method.getControlFlowGraph().getPredsOf(this);
  }

  public Collection<Statement> getSuccessors() {
    if (method == null) {
      throw new RuntimeException("Statement has no correct method");
    }

    return method.getControlFlowGraph().getSuccsOf(this);
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

  public abstract boolean isFieldStore();

  public abstract boolean isArrayStore();

  public abstract boolean isArrayLoad();

  public abstract boolean isFieldLoad();

  public abstract boolean isIdentityStmt();

  public abstract IInstanceFieldRef getFieldStore();

  public abstract IInstanceFieldRef getFieldLoad();

  public abstract boolean isStaticFieldLoad();

  public abstract boolean isStaticFieldStore();

  public abstract IStaticFieldRef getStaticField();

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

  public abstract int getLineNumber();

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
    if (baseHashCode != statement.baseHashCode) return false;
    return Objects.equals(method, statement.method);
  }

  @Override
  public int hashCode() {
    return baseHashCode;
  }
}
