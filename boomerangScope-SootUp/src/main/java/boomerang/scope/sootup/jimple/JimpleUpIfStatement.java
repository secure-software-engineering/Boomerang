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

import boomerang.scope.IfStatement;
import boomerang.scope.Statement;
import boomerang.scope.Val;
import java.util.Objects;
import sootup.core.jimple.basic.Value;
import sootup.core.jimple.common.constant.IntConstant;
import sootup.core.jimple.common.constant.NullConstant;
import sootup.core.jimple.common.expr.AbstractConditionExpr;
import sootup.core.jimple.common.expr.JEqExpr;
import sootup.core.jimple.common.expr.JNeExpr;
import sootup.core.jimple.common.stmt.JIfStmt;

public class JimpleUpIfStatement implements IfStatement {

  private final JIfStmt delegate;
  private final JimpleUpMethod method;

  public JimpleUpIfStatement(JIfStmt delegate, JimpleUpMethod method) {
    this.delegate = delegate;
    this.method = method;
  }

  @Override
  public Statement getTarget() {
    return JimpleUpStatement.create(
        delegate.getTargetStmts(method.getDelegate().getBody()).get(0), method);
  }

  @Override
  public Evaluation evaluate(Val val) {
    // TODO This requires a complete revisit because nobody knows what is happening here
    if (delegate.getCondition() instanceof JEqExpr) {
      JEqExpr eqExpr = (JEqExpr) delegate.getCondition();

      Value op1 = eqExpr.getOp1();
      Value op2 = eqExpr.getOp2();

      if ((val.equals(new JimpleUpVal(op1, method)) && op2.equals(NullConstant.getInstance()))
          || (val.equals(new JimpleUpVal(op2, method)) && op2.equals(NullConstant.getInstance()))) {
        return Evaluation.TRUE;
      }

      if ((val.equals(new JimpleUpVal(IntConstant.getInstance(0), method))
              && op2.equals(IntConstant.getInstance(0))
          || (val.equals(new JimpleUpVal(IntConstant.getInstance(1), method))
              && op2.equals(IntConstant.getInstance(1))))) {
        return Evaluation.TRUE;
      }

      if ((val.equals(new JimpleUpVal(IntConstant.getInstance(1), method))
              && op2.equals(IntConstant.getInstance(0))
          || (val.equals(new JimpleUpVal(IntConstant.getInstance(0), method))
              && op2.equals(IntConstant.getInstance(1))))) {
        return Evaluation.FALSE;
      }
    }

    if (delegate.getCondition() instanceof JNeExpr) {
      JNeExpr neExpr = (JNeExpr) delegate.getCondition();

      Value op1 = neExpr.getOp1();
      Value op2 = neExpr.getOp2();

      if ((val.equals(new JimpleUpVal(op1, method)) && op2.equals(NullConstant.getInstance())
          || (val.equals(new JimpleUpVal(op2, method))
              && op2.equals(NullConstant.getInstance())))) {
        return Evaluation.FALSE;
      }
      if ((val.equals(new JimpleUpVal(IntConstant.getInstance(0), method))
              && op2.equals(IntConstant.getInstance(0))
          || (val.equals(new JimpleUpVal(IntConstant.getInstance(1), method))
              && op2.equals(IntConstant.getInstance(1))))) {
        return Evaluation.FALSE;
      }
      if ((val.equals(new JimpleUpVal(IntConstant.getInstance(1), method))
              && op2.equals(IntConstant.getInstance(0))
          || (val.equals(new JimpleUpVal(IntConstant.getInstance(0), method))
              && op2.equals(IntConstant.getInstance(1))))) {
        return Evaluation.TRUE;
      }
    }

    return Evaluation.UNKNOWN;
  }

  @Override
  public boolean uses(Val val) {
    AbstractConditionExpr conditionExpr = delegate.getCondition();
    Value op1 = conditionExpr.getOp1();
    Value op2 = conditionExpr.getOp2();

    return val.equals(new JimpleUpVal(op1, method)) || val.equals(new JimpleUpVal(op2, method));
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    JimpleUpIfStatement that = (JimpleUpIfStatement) o;
    return Objects.equals(delegate, that.delegate);
  }

  @Override
  public int hashCode() {
    return Objects.hash(delegate);
  }

  @Override
  public String toString() {
    return delegate.toString();
  }
}
