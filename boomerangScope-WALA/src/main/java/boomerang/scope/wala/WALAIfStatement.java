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

import boomerang.scope.IfStatement;
import boomerang.scope.Statement;
import boomerang.scope.Val;
import com.ibm.wala.shrike.shrikeBT.IConditionalBranchInstruction;
import com.ibm.wala.ssa.SSAConditionalBranchInstruction;

public class WALAIfStatement implements IfStatement {

  private final SSAConditionalBranchInstruction delegate;
  private final WALAMethod method;
  private final Statement target;

  public WALAIfStatement(SSAConditionalBranchInstruction delegate, WALAMethod method) {
    this.delegate = delegate;
    this.method = method;
    this.target = method.getBranchTarget(delegate.getTarget());
  }

  @Override
  public Statement getTarget() {
    return target;
  }

  @Override
  public Evaluation evaluate(Val val) {
    WALAVal op1 = new WALAVal(delegate.getUse(0), method);
    WALAVal op2 = new WALAVal(delegate.getUse(1), method);
    if (delegate.getOperator().equals(IConditionalBranchInstruction.Operator.NE)) {
      if ((val.equals(op1) && op2.isNull()) || (val.equals(op2) && op1.isNull())) {
        return Evaluation.FALSE;
      }
    }

    if (delegate.getOperator().equals(IConditionalBranchInstruction.Operator.EQ)) {
      if ((val.equals(op1) && op2.isNull()) || (val.equals(op2) && op1.isNull())) {
        return Evaluation.TRUE;
      }
    }

    return Evaluation.UNKNOWN;
  }

  @Override
  public boolean uses(Val val) {
    WALAVal op1 = new WALAVal(delegate.getUse(0), method);
    WALAVal op2 = new WALAVal(delegate.getUse(1), method);
    return val.equals(op1) || val.equals(op2);
  }
}
