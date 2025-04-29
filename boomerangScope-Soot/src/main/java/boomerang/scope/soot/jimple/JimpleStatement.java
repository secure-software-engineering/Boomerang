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
package boomerang.scope.soot.jimple;

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
import soot.Value;
import soot.jimple.ArrayRef;
import soot.jimple.AssignStmt;
import soot.jimple.CastExpr;
import soot.jimple.CaughtExceptionRef;
import soot.jimple.IdentityStmt;
import soot.jimple.IfStmt;
import soot.jimple.InstanceFieldRef;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.NewExpr;
import soot.jimple.ReturnStmt;
import soot.jimple.StaticFieldRef;
import soot.jimple.Stmt;
import soot.jimple.ThrowStmt;

public class JimpleStatement extends Statement {

  private final Stmt delegate;
  private final JimpleMethod method;

  private JimpleStatement(Stmt delegate, JimpleMethod method) {
    super(method);
    if (delegate == null) {
      throw new RuntimeException("Invalid, parameter may not be null");
    }
    this.delegate = delegate;
    this.method = method;
  }

  public static Statement create(Stmt delegate, JimpleMethod method) {
    return new JimpleStatement(delegate, method);
  }

  @Override
  public boolean containsInvokeExpr() {
    return delegate.containsInvokeExpr();
  }

  @Override
  public Field getWrittenField() {
    AssignStmt as = (AssignStmt) delegate;
    if (as.getLeftOp() instanceof StaticFieldRef) {
      StaticFieldRef staticFieldRef = (StaticFieldRef) as.getLeftOp();
      return new JimpleField(method.getScene(), staticFieldRef.getFieldRef());
    }

    if (as.getLeftOp() instanceof ArrayRef) {
      return Field.array(getArrayBase().getIndex());
    }
    InstanceFieldRef ifr = (InstanceFieldRef) as.getLeftOp();
    return new JimpleField(method.getScene(), ifr.getFieldRef());
  }

  @Override
  public boolean isFieldWriteWithBase(Val base) {
    if (isFieldStore()) {
      IInstanceFieldRef fieldRef = getFieldStore();

      return fieldRef.getBase().equals(base);
    }

    if (isArrayStore()) {
      IArrayRef arrayRef = getArrayBase();

      return arrayRef.getBase().equals(base);
    }

    return false;
  }

  @Override
  public Field getLoadedField() {
    if (isFieldLoad()) {
      AssignStmt as = (AssignStmt) delegate;
      InstanceFieldRef ifr = (InstanceFieldRef) as.getRightOp();

      return new JimpleField(method.getScene(), ifr.getFieldRef());
    }

    throw new RuntimeException("Statement is not a field load statement");
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
    return delegate instanceof AssignStmt;
  }

  @Override
  public Val getLeftOp() {
    if (isAssignStmt()) {
      AssignStmt assignStmt = (AssignStmt) delegate;
      Value leftExpr = assignStmt.getLeftOp();

      if (leftExpr instanceof InstanceFieldRef) {
        return new JimpleInstanceFieldRef((InstanceFieldRef) leftExpr, method);
      }

      if (leftExpr instanceof ArrayRef) {
        return new JimpleArrayRef((ArrayRef) leftExpr, method);
      }

      if (leftExpr instanceof StaticFieldRef) {
        return new JimpleStaticFieldRef((StaticFieldRef) leftExpr, method);
      }

      return new JimpleVal(leftExpr, method);
    }

    throw new RuntimeException("Statement is not an assign statement");
  }

  @Override
  public Val getRightOp() {
    if (isAssignStmt()) {
      AssignStmt assignStmt = (AssignStmt) delegate;
      Value rightExpr = assignStmt.getRightOp();

      if (rightExpr instanceof InstanceFieldRef) {
        return new JimpleInstanceFieldRef((InstanceFieldRef) rightExpr, method);
      }

      if (rightExpr instanceof ArrayRef) {
        return new JimpleArrayRef((ArrayRef) rightExpr, method);
      }

      if (rightExpr instanceof StaticFieldRef) {
        return new JimpleStaticFieldRef((StaticFieldRef) rightExpr, method);
      }

      return new JimpleVal(rightExpr, method);
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
    return delegate instanceof AssignStmt
        && ((AssignStmt) delegate).getRightOp() instanceof CastExpr;
  }

  @Override
  public InvokeExpr getInvokeExpr() {
    return new JimpleInvokeExpr(delegate.getInvokeExpr(), method);
  }

  @Override
  public boolean isReturnStmt() {
    return delegate instanceof ReturnStmt;
  }

  @Override
  public boolean isThrowStmt() {
    return delegate instanceof ThrowStmt;
  }

  @Override
  public boolean isIfStmt() {
    return delegate instanceof IfStmt;
  }

  @Override
  public IfStatement getIfStmt() {
    return new JimpleIfStatement((IfStmt) delegate, method);
  }

  @Override
  public Val getReturnOp() {
    if (isReturnStmt()) {
      ReturnStmt assignStmt = (ReturnStmt) delegate;
      return new JimpleVal(assignStmt.getOp(), method);
    }

    throw new RuntimeException("Statement is not a return statement");
  }

  @Override
  public boolean isFieldStore() {
    return delegate instanceof AssignStmt
        && ((AssignStmt) delegate).getLeftOp() instanceof InstanceFieldRef;
  }

  @Override
  public boolean isArrayStore() {
    return delegate instanceof AssignStmt
        && ((AssignStmt) delegate).getLeftOp() instanceof ArrayRef;
  }

  @Override
  public boolean isArrayLoad() {
    return delegate instanceof AssignStmt
        && ((AssignStmt) delegate).getRightOp() instanceof ArrayRef;
  }

  @Override
  public boolean isFieldLoad() {
    return delegate instanceof AssignStmt
        && ((AssignStmt) delegate).getRightOp() instanceof InstanceFieldRef;
  }

  @Override
  public boolean isIdentityStmt() {
    return delegate instanceof IdentityStmt;
  }

  /**
   * This method kills a data-flow at an if-stmt, it is assumed that the propagated "allocation"
   * site is x = null and fact is the propagated aliased variable. (i.e., y after a statement y =
   * x). If the if-stmt checks for if y != null or if y == null, data-flow propagation can be killed
   * when along the true/false branch.
   *
   * @param fact The data-flow value that bypasses the if-stmt
   * @param successor The successor statement of the if-stmt
   * @return true if the Val fact shall be killed
   */
  @Deprecated
  public boolean killAtIfStmt(Val fact, Statement successor) {
    //		IfStmt ifStmt = this.getIfStmt();
    //		if(successor instanceof CallSiteStatement) {
    //          successor = ((CallSiteStatement) successor).getDelegate();
    //		} else if(successor instanceof ReturnSiteStatement) {
    //          successor = ((ReturnSiteStatement) successor).getDelegate();
    //        }
    //		Stmt succ = ((JimpleStatement)successor).getDelegate();
    //		Stmt target = ifStmt.getTarget();
    //
    //		Value condition = ifStmt.getCondition();
    //		if (condition instanceof JEqExpr) {
    //			JEqExpr eqExpr = (JEqExpr) condition;
    //			Value op1 = eqExpr.getOp1();
    //			Value op2 = eqExpr.getOp2();
    //			Val jop1 = new JimpleVal(eqExpr.getOp1(), successor.getMethod());
    //			Val jop2 = new JimpleVal(eqExpr.getOp2(), successor.getMethod());
    //			if (fact instanceof JimpleDoubleVal) {
    //				JimpleDoubleVal valWithFalseVar = (JimpleDoubleVal) fact;
    //				if (jop1.equals(valWithFalseVar.getFalseVariable())) {
    //					if (op2.equals(IntConstant.v(0))) {
    //						if (!succ.equals(target)) {
    //							return true;
    //						}
    //					}
    //				}
    //				if (jop2.equals(valWithFalseVar.getFalseVariable())) {
    //					if (op1.equals(IntConstant.v(0))) {
    //						if (!succ.equals(target)) {
    //							return true;
    //						}
    //					}
    //				}
    //			}
    //			if (op1 instanceof NullConstant) {
    //				if (new JimpleVal(op2,successor.getMethod()).equals(fact)) {
    //					if (!succ.equals(target)) {
    //						return true;
    //					}
    //				}
    //			} else if (op2 instanceof NullConstant) {
    //				if (new JimpleVal(op1,successor.getMethod()).equals(fact)) {
    //					if (!succ.equals(target)) {
    //						return true;
    //					}
    //				}
    //			}
    //		}
    //		if (condition instanceof JNeExpr) {
    //			JNeExpr eqExpr = (JNeExpr) condition;
    //			Value op1 = eqExpr.getOp1();
    //			Value op2 = eqExpr.getOp2();
    //			if (op1 instanceof NullConstant) {
    //				if (new JimpleVal(op2,successor.getMethod()).equals(fact)) {
    //					if (succ.equals(target)) {
    //						return true;
    //					}
    //				}
    //			} else if (op2 instanceof NullConstant) {
    //				if (new JimpleVal(op1,successor.getMethod()).equals(fact)) {
    //					if (succ.equals(target)) {
    //						return true;
    //					}
    //				}
    //			}
    //		}
    return false;
  }

  @Override
  public IInstanceFieldRef getFieldStore() {
    if (isFieldStore()) {
      AssignStmt ins = (AssignStmt) delegate;
      InstanceFieldRef fieldRef = (InstanceFieldRef) ins.getLeftOp();

      return new JimpleInstanceFieldRef(fieldRef, method);
    }

    throw new RuntimeException("Statement is not a field store statement");
  }

  @Override
  public IInstanceFieldRef getFieldLoad() {
    if (isFieldLoad()) {
      AssignStmt ins = (AssignStmt) delegate;
      soot.jimple.InstanceFieldRef fieldRef = (soot.jimple.InstanceFieldRef) ins.getRightOp();

      return new JimpleInstanceFieldRef(fieldRef, method);
    }

    throw new RuntimeException("Statement is not a field load statement");
  }

  @Override
  public boolean isStaticFieldLoad() {
    return delegate instanceof AssignStmt
        && ((AssignStmt) delegate).getRightOp() instanceof StaticFieldRef;
  }

  @Override
  public boolean isStaticFieldStore() {
    return delegate instanceof AssignStmt
        && ((AssignStmt) delegate).getLeftOp() instanceof StaticFieldRef;
  }

  @Override
  public IStaticFieldRef getStaticField() {
    StaticFieldRef v;
    if (isStaticFieldLoad()) {
      v = (StaticFieldRef) ((AssignStmt) delegate).getRightOp();
    } else if (isStaticFieldStore()) {
      v = (StaticFieldRef) ((AssignStmt) delegate).getLeftOp();
    } else {
      throw new RuntimeException("Statement has no static field access");
    }

    return new JimpleStaticFieldRef(v, method);
  }

  @Override
  public boolean isPhiStatement() {
    return false;
  }

  @Override
  public Collection<Val> getPhiVals() {
    throw new RuntimeException("Not supported!");
  }

  @Override
  public IArrayRef getArrayBase() {
    if (isArrayLoad()) {
      AssignStmt assignStmt = (AssignStmt) delegate;
      ArrayRef arrayRef = (ArrayRef) assignStmt.getRightOp();

      return new JimpleArrayRef(arrayRef, method);
    }

    if (isArrayStore()) {
      AssignStmt assignStmt = (AssignStmt) delegate;
      ArrayRef arrayRef = (ArrayRef) assignStmt.getLeftOp();

      return new JimpleArrayRef(arrayRef, method);
    }

    throw new RuntimeException("Statement has no array base");
  }

  @Override
  public int getLineNumber() {
    return delegate.getJavaSourceStartLineNumber();
  }

  @Override
  public boolean isCatchStmt() {
    return delegate instanceof IdentityStmt
        && ((IdentityStmt) delegate).getRightOp() instanceof CaughtExceptionRef;
  }

  public Stmt getDelegate() {
    return delegate;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    JimpleStatement that = (JimpleStatement) o;
    return Objects.equals(delegate, that.delegate) && Objects.equals(method, that.method);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), delegate, method);
  }

  @Override
  public String toString() {
    return shortName(delegate);
  }

  private String shortName(Stmt s) {
    if (s.containsInvokeExpr()) {
      String base = "";
      if (s.getInvokeExpr() instanceof InstanceInvokeExpr) {
        InstanceInvokeExpr iie = (InstanceInvokeExpr) s.getInvokeExpr();
        base = iie.getBase().toString() + ".";
      }
      String assign = "";
      if (s instanceof AssignStmt) {
        assign = ((AssignStmt) s).getLeftOp() + " = ";
      }
      return assign
          + base
          + s.getInvokeExpr().getMethod().getName()
          + "("
          + Joiner.on(",").join(s.getInvokeExpr().getArgs())
          + ")";
    }
    if (s instanceof IdentityStmt) {
      return s.toString();
    }
    if (s instanceof AssignStmt) {
      AssignStmt assignStmt = (AssignStmt) s;
      if (assignStmt.getLeftOp() instanceof InstanceFieldRef) {
        InstanceFieldRef ifr = (InstanceFieldRef) assignStmt.getLeftOp();
        return ifr.getBase() + "." + ifr.getField().getName() + " = " + assignStmt.getRightOp();
      }
      if (assignStmt.getRightOp() instanceof InstanceFieldRef) {
        InstanceFieldRef ifr = (InstanceFieldRef) assignStmt.getRightOp();
        return assignStmt.getLeftOp() + " = " + ifr.getBase() + "." + ifr.getField().getName();
      }
      if (assignStmt.getRightOp() instanceof NewExpr) {
        NewExpr newExpr = (NewExpr) assignStmt.getRightOp();
        return assignStmt.getLeftOp()
            + " = new "
            + newExpr.getBaseType().getSootClass().getShortName();
      }
    }
    return s.toString();
  }
}
