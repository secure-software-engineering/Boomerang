package boomerang.scope.opal.tac

import com.google.common.base.Joiner
import org.opalj.tac.{Nop, Param, PutField, Return}

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

      return s"$assign$base${stmt.getInvokeExpr.getMethod.getName}(${Joiner.on(",").join(stmt.getInvokeExpr.getArgs)})"
    }

    if (stmt.isAssignStmt) {
      if (delegate.isAssignment) {
        if (stmt.isFieldStore) {
          return s"${stmt.getLeftOp} = ${stmt.getFieldStore.getX}.${stmt.getFieldStore.getY}"
        } else if (stmt.isArrayStore) {
          val base = stmt.getArrayBase
          return s"${base.getX.getVariableName}[${base.getY}] = ${stmt.getRightOp}"
        } else {
          return s"${stmt.getLeftOp} = ${stmt.getRightOp}"
        }
      }
    }

    if (delegate.isAssignment) {
      if (delegate.asAssignment.expr.isVar && delegate.asAssignment.expr.asVar.isParameterLocal) {
        return s"${delegate.asAssignment.targetVar} := @${delegate.asAssignment.expr}: ${stmt.getMethod}"
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
      return s"${delegate.pc}: return ${stmt.getReturnOp}"
    }

    delegate.toString
  }

}
