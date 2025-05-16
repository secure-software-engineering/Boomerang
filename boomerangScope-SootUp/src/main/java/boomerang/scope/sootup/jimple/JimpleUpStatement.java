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
package boomerang.scope.sootup.jimple;

import boomerang.scope.Field;
import boomerang.scope.IArrayRef;
import boomerang.scope.IInstanceFieldRef;
import boomerang.scope.IStaticFieldRef;
import boomerang.scope.IfStatement;
import boomerang.scope.InvokeExpr;
import boomerang.scope.Statement;
import boomerang.scope.Val;
import com.google.common.base.Joiner;
import java.util.Collection;
import java.util.Objects;
import sootup.core.jimple.basic.Value;
import sootup.core.jimple.common.expr.*;
import sootup.core.jimple.common.ref.JArrayRef;
import sootup.core.jimple.common.ref.JCaughtExceptionRef;
import sootup.core.jimple.common.ref.JInstanceFieldRef;
import sootup.core.jimple.common.ref.JStaticFieldRef;
import sootup.core.jimple.common.stmt.JAssignStmt;
import sootup.core.jimple.common.stmt.JIdentityStmt;
import sootup.core.jimple.common.stmt.JIfStmt;
import sootup.core.jimple.common.stmt.JReturnStmt;
import sootup.core.jimple.common.stmt.JThrowStmt;
import sootup.core.jimple.common.stmt.Stmt;

public class JimpleUpStatement extends Statement {

  private final Stmt delegate;
  private final JimpleUpMethod method;

  private JimpleUpStatement(Stmt delegate, JimpleUpMethod method) {
    super(method);

    if (delegate == null) {
      throw new RuntimeException("Statement must not be null");
    }

    this.delegate = delegate;
    this.method = method;
  }

  public static Statement create(Stmt delegate, JimpleUpMethod method) {
    return new JimpleUpStatement(delegate, method);
  }

  public Stmt getDelegate() {
    return delegate;
  }

  @Override
  public boolean containsInvokeExpr() {
    return delegate.isInvokableStmt() && delegate.asInvokableStmt().containsInvokeExpr();
  }

  @Override
  public Field getWrittenField() {
    assert isAssignStmt();

    JAssignStmt assignStmt = (JAssignStmt) delegate;
    if (assignStmt.getLeftOp() instanceof JStaticFieldRef) {
      JStaticFieldRef staticFieldRef = (JStaticFieldRef) assignStmt.getLeftOp();
      return new JimpleUpField(staticFieldRef.getFieldSignature(), method.getView());
    }

    if (assignStmt.getLeftOp() instanceof JArrayRef) {
      return Field.array(getArrayBase().getIndex());
    }

    JInstanceFieldRef ifr = (JInstanceFieldRef) assignStmt.getLeftOp();
    return new JimpleUpField(ifr.getFieldSignature(), method.getView());
  }

  @Override
  public boolean isFieldWriteWithBase(Val base) {
    if (isFieldStore()) {
      IInstanceFieldRef fieldRef = getFieldStore();
      return fieldRef.getBase().equals(base);
    }

    if (isAssignStmt() && isArrayStore()) {
      IArrayRef arrayBase = getArrayBase();
      return arrayBase.getBase().equals(base);
    }

    return false;
  }

  @Override
  public Field getLoadedField() {
    JAssignStmt as = (JAssignStmt) delegate;
    JInstanceFieldRef ifr = (JInstanceFieldRef) as.getRightOp();

    return new JimpleUpField(ifr.getFieldSignature(), method.getView());
  }

  @Override
  public boolean isFieldLoadWithBase(Val base) {
    if (isFieldLoad()) {
      IInstanceFieldRef fieldRef = getFieldLoad();

      return fieldRef.getBase().equals(base);
    }

    return false;
  }

  @Override
  public boolean isAssignStmt() {
    return delegate instanceof JAssignStmt;
  }

  @Override
  public Val getLeftOp() {
    if (isAssignStmt()) {
      JAssignStmt assignStmt = (JAssignStmt) delegate;
      Value leftOp = assignStmt.getLeftOp();

      if (leftOp instanceof JInstanceFieldRef) {
        return new JimpleUpInstanceFieldRef((JInstanceFieldRef) leftOp, method);
      }

      if (leftOp instanceof JArrayRef) {
        return new JimpleUpArrayRef((JArrayRef) leftOp, method);
      }

      if (leftOp instanceof JStaticFieldRef) {
        return new JimpleUpStaticFieldRef((JStaticFieldRef) leftOp, method);
      }

      return new JimpleUpVal(assignStmt.getLeftOp(), method);
    }

    throw new RuntimeException("Statement is not an assign statement");
  }

  @Override
  public Val getRightOp() {
    if (isAssignStmt()) {
      JAssignStmt assignStmt = (JAssignStmt) delegate;
      Value rightOp = assignStmt.getRightOp();

      if (rightOp instanceof JInstanceFieldRef) {
        return new JimpleUpInstanceFieldRef((JInstanceFieldRef) rightOp, method);
      }

      if (rightOp instanceof JArrayRef) {
        return new JimpleUpArrayRef((JArrayRef) rightOp, method);
      }

      if (rightOp instanceof JStaticFieldRef) {
        return new JimpleUpStaticFieldRef((JStaticFieldRef) rightOp, method);
      }

      return new JimpleUpVal(assignStmt.getRightOp(), method);
    }

    throw new RuntimeException("Statement is not an assign statement");
  }

  @Override
  public boolean isInstanceOfStatement(Val fact) {
    if (isAssignStmt()) {
      if (getRightOp().isInstanceOfExpr()) {
        Val instanceOfOp = getRightOp().getInstanceOfOp();
        return instanceOfOp.equals(fact);
      }
    }
    return false;
  }

  @Override
  public boolean isCast() {
    if (delegate instanceof JAssignStmt) {
      JAssignStmt assignStmt = (JAssignStmt) delegate;

      return assignStmt.getRightOp() instanceof JCastExpr;
    }
    return false;
  }

  @Override
  public boolean isPhiStatement() {
    return false;
  }

  @Override
  public InvokeExpr getInvokeExpr() {
    assert containsInvokeExpr();
    assert delegate.isInvokableStmt();
    assert delegate.asInvokableStmt().getInvokeExpr().isPresent();
    return new JimpleUpInvokeExpr(delegate.asInvokableStmt().getInvokeExpr().get(), method);
  }

  @Override
  public boolean isReturnStmt() {
    return delegate instanceof JReturnStmt;
  }

  @Override
  public boolean isThrowStmt() {
    return delegate instanceof JThrowStmt;
  }

  @Override
  public boolean isIfStmt() {
    return delegate instanceof JIfStmt;
  }

  @Override
  public IfStatement getIfStmt() {
    assert isIfStmt();

    JIfStmt ifStmt = (JIfStmt) delegate;
    return new JimpleUpIfStatement(ifStmt, method);
  }

  @Override
  public Val getReturnOp() {
    assert isReturnStmt();

    JReturnStmt returnStmt = (JReturnStmt) delegate;
    return new JimpleUpVal(returnStmt.getOp(), method);
  }

  @Override
  public boolean isFieldStore() {
    return delegate instanceof JAssignStmt
        && ((JAssignStmt) delegate).getLeftOp() instanceof JInstanceFieldRef;
  }

  @Override
  public boolean isArrayStore() {
    return delegate instanceof JAssignStmt
        && ((JAssignStmt) delegate).getLeftOp() instanceof JArrayRef;
  }

  @Override
  public boolean isArrayLoad() {
    return delegate instanceof JAssignStmt
        && ((JAssignStmt) delegate).getRightOp() instanceof JArrayRef;
  }

  @Override
  public boolean isFieldLoad() {
    return delegate instanceof JAssignStmt
        && ((JAssignStmt) delegate).getRightOp() instanceof JInstanceFieldRef;
  }

  @Override
  public boolean isIdentityStmt() {
    return delegate instanceof JIdentityStmt;
  }

  @Override
  public IInstanceFieldRef getFieldStore() {
    JAssignStmt assignStmt = (JAssignStmt) delegate;
    JInstanceFieldRef fieldRef = (JInstanceFieldRef) assignStmt.getLeftOp();

    return new JimpleUpInstanceFieldRef(fieldRef, method);
  }

  @Override
  public IInstanceFieldRef getFieldLoad() {
    JAssignStmt assignStmt = (JAssignStmt) delegate;
    JInstanceFieldRef fieldRef = (JInstanceFieldRef) assignStmt.getRightOp();

    return new JimpleUpInstanceFieldRef(fieldRef, method);
  }

  @Override
  public boolean isStaticFieldLoad() {
    return delegate instanceof JAssignStmt
        && ((JAssignStmt) delegate).getRightOp() instanceof JStaticFieldRef;
  }

  @Override
  public boolean isStaticFieldStore() {
    return delegate instanceof JAssignStmt
        && ((JAssignStmt) delegate).getLeftOp() instanceof JStaticFieldRef;
  }

  @Override
  public IStaticFieldRef getStaticField() {
    JStaticFieldRef v;
    if (isStaticFieldLoad()) {
      v = (JStaticFieldRef) ((JAssignStmt) delegate).getRightOp();
    } else if (isStaticFieldStore()) {
      v = (JStaticFieldRef) ((JAssignStmt) delegate).getLeftOp();
    } else {
      throw new RuntimeException("Statement does not have a static field");
    }

    return new JimpleUpStaticFieldRef(v, method);
  }

  @Override
  public boolean killAtIfStmt(Val fact, Statement successor) {
    return false;
  }

  @Override
  public Collection<Val> getPhiVals() {
    throw new RuntimeException("Not supported!");
  }

  @Override
  public IArrayRef getArrayBase() {
    if (isArrayLoad()) {
      JAssignStmt assignStmt = (JAssignStmt) delegate;
      JArrayRef arrayRef = (JArrayRef) assignStmt.getRightOp();

      return new JimpleUpArrayRef(arrayRef, method);
    }

    if (isArrayStore()) {
      JAssignStmt assignStmt = (JAssignStmt) delegate;
      JArrayRef arrayRef = (JArrayRef) assignStmt.getLeftOp();

      return new JimpleUpArrayRef(arrayRef, method);
    }

    throw new RuntimeException("Statement does not deal with an array base");
  }

  @Override
  public int getLineNumber() {
    return delegate.getPositionInfo().getStmtPosition().getFirstLine();
  }

  @Override
  public boolean isCatchStmt() {
    return delegate instanceof JIdentityStmt
        && ((JIdentityStmt) delegate).getRightOp() instanceof JCaughtExceptionRef;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    JimpleUpStatement that = (JimpleUpStatement) o;
    return Objects.equals(delegate, that.delegate);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), delegate);
  }

  @Override
  public String toString() {
    return shortName(delegate);
  }

  private String shortName(Stmt s) {
    if (s.isInvokableStmt() && s.asInvokableStmt().containsInvokeExpr()) {
      String base = "";
      AbstractInvokeExpr abstractInvokeExpr = s.asInvokableStmt().getInvokeExpr().get();
      if (abstractInvokeExpr instanceof AbstractInstanceInvokeExpr) {
        AbstractInstanceInvokeExpr iie = (AbstractInstanceInvokeExpr) abstractInvokeExpr;
        base = iie.getBase() + ".";
      }
      String assign = "";
      if (s instanceof JAssignStmt) {
        assign = ((JAssignStmt) s).getLeftOp() + " = ";
      }
      return assign
          + base
          + abstractInvokeExpr.getMethodSignature().getName()
          + "("
          + Joiner.on(",").join(abstractInvokeExpr.getArgs())
          + ")";
    }
    if (s instanceof JIdentityStmt) {
      return s.toString();
    }
    if (s instanceof JAssignStmt) {
      JAssignStmt assignStmt = (JAssignStmt) s;
      if (assignStmt.getLeftOp() instanceof JInstanceFieldRef) {
        JInstanceFieldRef ifr = (JInstanceFieldRef) assignStmt.getLeftOp();
        return ifr.getBase()
            + "."
            + ifr.getFieldSignature().getName()
            + " = "
            + assignStmt.getRightOp();
      }
      if (assignStmt.getRightOp() instanceof JInstanceFieldRef) {
        JInstanceFieldRef ifr = (JInstanceFieldRef) assignStmt.getRightOp();
        return assignStmt.getLeftOp()
            + " = "
            + ifr.getBase()
            + "."
            + ifr.getFieldSignature().getName();
      }
      if (assignStmt.getRightOp() instanceof JNewExpr) {
        JNewExpr newExpr = (JNewExpr) assignStmt.getRightOp();
        return assignStmt.getLeftOp()
            + " = new "
            + newExpr.getType().getClassName(); // getSootClass().getShortName();
      }
    }
    return s.toString();
  }
}
