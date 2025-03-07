/**
 * ***************************************************************************** Copyright (c) 2018
 * Fraunhofer IEM, Paderborn, Germany. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * <p>SPDX-License-Identifier: EPL-2.0
 *
 * <p>Contributors: Johannes Spaeth - initial API and implementation
 * *****************************************************************************
 */
package boomerang.scope.soot.jimple;

import boomerang.scope.Field;
import boomerang.scope.IfStatement;
import boomerang.scope.InvokeExpr;
import boomerang.scope.Method;
import boomerang.scope.Pair;
import boomerang.scope.Statement;
import boomerang.scope.StaticFieldVal;
import boomerang.scope.Val;
import com.google.common.base.Joiner;
import java.util.Collection;
import java.util.Objects;
import soot.jimple.ArrayRef;
import soot.jimple.AssignStmt;
import soot.jimple.CastExpr;
import soot.jimple.CaughtExceptionRef;
import soot.jimple.IdentityStmt;
import soot.jimple.IfStmt;
import soot.jimple.InstanceFieldRef;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.NewExpr;
import soot.jimple.NewMultiArrayExpr;
import soot.jimple.ReturnStmt;
import soot.jimple.StaticFieldRef;
import soot.jimple.Stmt;
import soot.jimple.ThrowStmt;
import soot.tagkit.SourceLnPosTag;

public class JimpleStatement extends Statement {

  private final Stmt delegate;
  private final Method method;

  private JimpleStatement(Stmt delegate, Method m) {
    super(m);
    if (delegate == null) {
      throw new RuntimeException("Invalid, parameter may not be null");
    }
    this.delegate = delegate;
    this.method = m;
  }

  public static Statement create(Stmt delegate, Method m) {
    return new JimpleStatement(delegate, m);
  }

  @Override
  public boolean containsStaticFieldAccess() {
    if (delegate instanceof AssignStmt) {
      AssignStmt assignStmt = (AssignStmt) delegate;
      return assignStmt.getLeftOp() instanceof StaticFieldRef
          || assignStmt.getRightOp() instanceof StaticFieldRef;
    }
    return false;
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
      return new JimpleField(staticFieldRef.getField());
    }

    if (as.getLeftOp() instanceof ArrayRef) {
      return Field.array(getArrayBase().getY());
    }
    InstanceFieldRef ifr = (InstanceFieldRef) as.getLeftOp();
    return new JimpleField(ifr.getField());
  }

  @Override
  public boolean isFieldWriteWithBase(Val base) {
    if (isAssignStmt() && isFieldStore()) {
      Pair<Val, Field> instanceFieldRef = getFieldStore();
      return instanceFieldRef.getX().equals(base);
    }
    if (isAssignStmt() && isArrayStore()) {
      Pair<Val, Integer> arrayBase = getArrayBase();
      return arrayBase.getX().equals(base);
    }
    return false;
  }

  @Override
  public Field getLoadedField() {
    if (isFieldLoad()) {
      AssignStmt as = (AssignStmt) delegate;
      InstanceFieldRef ifr = (InstanceFieldRef) as.getRightOp();
      return new JimpleField(ifr.getField());
    }

    throw new RuntimeException("Statement is not a field load statement");
  }

  @Override
  public boolean isFieldLoadWithBase(Val base) {
    if (isAssignStmt() && isFieldLoad()) {
      return getFieldLoad().getX().equals(base);
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
      return new JimpleVal(assignStmt.getLeftOp(), method);
    }

    throw new RuntimeException("Statement is not an assign statement");
  }

  @Override
  public Val getRightOp() {
    if (isAssignStmt()) {
      AssignStmt assignStmt = (AssignStmt) delegate;
      return new JimpleVal(assignStmt.getRightOp(), method);
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
  public boolean isMultiArrayAllocation() {
    return (delegate instanceof AssignStmt)
        && ((AssignStmt) delegate).getRightOp() instanceof NewMultiArrayExpr;
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

  public String getShortLabel() {
    if (delegate instanceof AssignStmt) {
      AssignStmt assignStmt = (AssignStmt) delegate;
      if (assignStmt.getRightOp() instanceof InstanceFieldRef) {
        InstanceFieldRef fr = (InstanceFieldRef) assignStmt.getRightOp();
        return assignStmt.getLeftOp() + " = " + fr.getBase() + "." + fr.getField().getName();
      }
      if (assignStmt.getLeftOp() instanceof InstanceFieldRef) {
        InstanceFieldRef fr = (InstanceFieldRef) assignStmt.getLeftOp();
        return fr.getBase() + "." + fr.getField().getName() + " = " + assignStmt.getRightOp();
      }
    }
    if (containsInvokeExpr()) {
      InvokeExpr invokeExpr = getInvokeExpr();
      if (invokeExpr.isStaticInvokeExpr()) {
        return (isAssignStmt() ? getLeftOp() + " = " : "")
            + invokeExpr.getMethod()
            + "("
            + invokeExpr.getArgs().toString().replace("[", "").replace("]", "")
            + ")";
      }
      if (invokeExpr.isInstanceInvokeExpr()) {
        return (isAssignStmt() ? getLeftOp() + " = " : "")
            + invokeExpr.getBase()
            + "."
            + invokeExpr.getMethod()
            + "("
            + invokeExpr.getArgs().toString().replace("[", "").replace("]", "")
            + ")";
      }
    }
    return delegate.toString();
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
  public Pair<Val, Field> getFieldStore() {
    if (isFieldStore()) {
      AssignStmt ins = (AssignStmt) delegate;
      soot.jimple.InstanceFieldRef val = (soot.jimple.InstanceFieldRef) ins.getLeftOp();
      return new Pair<>(new JimpleVal(val.getBase(), method), new JimpleField(val.getField()));
    }

    throw new RuntimeException("Statement is not a field store statement");
  }

  @Override
  public Pair<Val, Field> getFieldLoad() {
    if (isFieldLoad()) {
      AssignStmt ins = (AssignStmt) delegate;
      soot.jimple.InstanceFieldRef val = (soot.jimple.InstanceFieldRef) ins.getRightOp();
      return new Pair<>(new JimpleVal(val.getBase(), method), new JimpleField(val.getField()));
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
  public StaticFieldVal getStaticField() {
    StaticFieldRef v;
    if (isStaticFieldLoad()) {
      v = (StaticFieldRef) ((AssignStmt) delegate).getRightOp();
    } else if (isStaticFieldStore()) {
      v = (StaticFieldRef) ((AssignStmt) delegate).getLeftOp();
    } else {
      throw new RuntimeException("Statement has no static field access");
    }
    return new JimpleStaticFieldVal(new JimpleField(v.getField()), method);
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
  public Pair<Val, Integer> getArrayBase() {
    if (isArrayLoad()) {
      Val rightOp = getRightOp();
      return rightOp.getArrayBase();
    }
    if (isArrayStore()) {
      Val rightOp = getLeftOp();
      return rightOp.getArrayBase();
    }
    throw new RuntimeException("Statement has no array base");
  }

  @Override
  public int getStartLineNumber() {
    return delegate.getJavaSourceStartLineNumber();
  }

  @Override
  public int getStartColumnNumber() {
    return delegate.getJavaSourceStartColumnNumber();
  }

  @Override
  public int getEndColumnNumber() {
    // TODO move to Soot
    SourceLnPosTag tag = (SourceLnPosTag) delegate.getTag("SourceLnPosTag");
    if (tag != null) {
      return tag.endPos();
    }
    return -1;
  }

  @Override
  public int getEndLineNumber() {
    // TODO move to Soot
    SourceLnPosTag tag = (SourceLnPosTag) delegate.getTag("SourceLnPosTag");
    if (tag != null) {
      return tag.endLn();
    }
    return -1;
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
