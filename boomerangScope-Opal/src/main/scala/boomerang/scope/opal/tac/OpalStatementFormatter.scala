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
package boomerang.scope.opal.tac

import com.google.common.base.Joiner
import org.opalj.tac.Nop
import org.opalj.tac.Param
import org.opalj.tac.PutField
import org.opalj.tac.Return

object OpalStatementFormatter {

  def apply(stmt: OpalStatement): String = {
    val delegate = stmt.delegate

    if (stmt.containsInvokeExpr()) {
      var base = ""
      if (stmt.getInvokeExpr.isInstanceInvokeExpr) {
        base = s"${stmt.getInvokeExpr.getBase}."
      }
      var assign = ""
      if (stmt.isAssignStmt) {
        assign = s"${stmt.getLeftOp} = "
      }

      return s"$assign$base${stmt.getInvokeExpr.getDeclaredMethod.getName}(${Joiner.on(",").join(stmt.getInvokeExpr.getArgs)})"
    }

    if (stmt.isAssignStmt) {
      if (delegate.isAssignment) {
        return s"${stmt.getLeftOp} = ${stmt.getRightOp}"
      }

      if (stmt.isFieldStore) {
        return s"${stmt.getLeftOp} = ${stmt.getRightOp}"
      }

      if (stmt.isArrayStore) {
        val base = stmt.getArrayBase
        return s"${base.getX.getVariableName}[${base.getY}] = ${stmt.getRightOp}"
      }

      if (stmt.isStaticFieldStore) {
        return s"${stmt.getLeftOp} = ${stmt.getRightOp}"
      }
    }

    if (delegate.isAssignment) {
      if (delegate.asAssignment.expr.isVar && delegate.asAssignment.expr.asVar.isParameterLocal) {
        return s"${delegate.asAssignment.targetVar} := @${delegate.asAssignment.expr}: ${stmt.getMethod}"
      }

      if (delegate.asAssignment.expr.isVar && delegate.asAssignment.expr.asVar.isExceptionLocal) {
        return s"${delegate.asAssignment.targetVar} := @caughtException: ${delegate.asAssignment.expr}"
      }
    }

    if (delegate.astID == PutField.ASTID) {
      return s"${stmt.getLeftOp} = ${stmt.getRightOp}"
    }

    if (delegate.astID == Nop.ASTID) {
      return "nop"
    }

    if (delegate.astID == Return.ASTID) {
      return "return"
    }

    if (stmt.isReturnStmt) {
      return s"return ${stmt.getReturnOp}"
    }

    delegate.toString
  }

}
