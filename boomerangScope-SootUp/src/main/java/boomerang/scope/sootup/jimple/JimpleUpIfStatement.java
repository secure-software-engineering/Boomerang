package boomerang.scope.sootup.jimple;

import boomerang.scope.IfStatement;
import boomerang.scope.Statement;
import boomerang.scope.Val;
import java.util.Arrays;
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
  public int hashCode() {
    return Arrays.hashCode(new Object[] {delegate});
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;

    JimpleUpIfStatement other = (JimpleUpIfStatement) obj;
    if (delegate == null) {
      return other.delegate == null;
    } else return delegate.equals(other.delegate);
  }

  @Override
  public String toString() {
    return delegate.toString();
  }
}
