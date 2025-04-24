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

import boomerang.scope.IfStatement;
import boomerang.scope.Method;
import boomerang.scope.Statement;
import boomerang.scope.Val;
import java.util.Objects;
import soot.Value;
import soot.jimple.ConditionExpr;
import soot.jimple.EqExpr;
import soot.jimple.IfStmt;
import soot.jimple.IntConstant;
import soot.jimple.NeExpr;
import soot.jimple.NullConstant;

public class JimpleIfStatement implements IfStatement {

  private final IfStmt delegate;
  private final Method method;

  public JimpleIfStatement(IfStmt delegate, Method method) {
    this.delegate = delegate;
    this.method = method;
  }

  @Override
  public Statement getTarget() {
    return JimpleStatement.create(delegate.getTarget(), method);
  }

  @Override
  public Evaluation evaluate(Val val) {
    if (delegate.getCondition() instanceof EqExpr) {
      EqExpr eqExpr = (EqExpr) delegate.getCondition();
      Value op1 = eqExpr.getOp1();
      Value op2 = eqExpr.getOp2();
      if ((val.equals(new JimpleVal(op1, method)) && op2.equals(NullConstant.v())
          || (val.equals(new JimpleVal(op2, method)) && op2.equals(NullConstant.v())))) {
        return Evaluation.TRUE;
      }
      if ((val.equals(new JimpleVal(IntConstant.v(0), method)) && op2.equals(IntConstant.v(0))
          || (val.equals(new JimpleVal(IntConstant.v(1), method))
              && op2.equals(IntConstant.v(1))))) {
        return Evaluation.TRUE;
      }
      if ((val.equals(new JimpleVal(IntConstant.v(1), method)) && op2.equals(IntConstant.v(0))
          || (val.equals(new JimpleVal(IntConstant.v(0), method))
              && op2.equals(IntConstant.v(1))))) {
        return Evaluation.FALSE;
      }
    }

    if (delegate.getCondition() instanceof NeExpr) {
      NeExpr eqExpr = (NeExpr) delegate.getCondition();
      Value op1 = eqExpr.getOp1();
      Value op2 = eqExpr.getOp2();
      if ((val.equals(new JimpleVal(op1, method)) && op2.equals(NullConstant.v())
          || (val.equals(new JimpleVal(op2, method)) && op2.equals(NullConstant.v())))) {
        return Evaluation.FALSE;
      }
      if ((val.equals(new JimpleVal(IntConstant.v(0), method)) && op2.equals(IntConstant.v(0))
          || (val.equals(new JimpleVal(IntConstant.v(1), method))
              && op2.equals(IntConstant.v(1))))) {
        return Evaluation.FALSE;
      }
      if ((val.equals(new JimpleVal(IntConstant.v(1), method)) && op2.equals(IntConstant.v(0))
          || (val.equals(new JimpleVal(IntConstant.v(0), method))
              && op2.equals(IntConstant.v(1))))) {
        return Evaluation.TRUE;
      }
    }
    return Evaluation.UNKNOWN;
  }

  @Override
  public boolean uses(Val val) {
    if (delegate.getCondition() instanceof ConditionExpr) {
      ConditionExpr c = ((ConditionExpr) delegate.getCondition());
      Value op1 = c.getOp1();
      Value op2 = c.getOp2();
      return val.equals(new JimpleVal(op1, method)) || val.equals(new JimpleVal(op2, method));
    }
    return false;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    JimpleIfStatement that = (JimpleIfStatement) o;
    return Objects.equals(delegate, that.delegate) && Objects.equals(method, that.method);
  }

  @Override
  public int hashCode() {
    return Objects.hash(delegate, method);
  }
}
