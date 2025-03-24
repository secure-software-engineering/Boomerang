package boomerang.scope.opal.tac

import com.google.common.base.Joiner
import org.opalj.tac.{Nop, Return}

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

      return s"${delegate.pc}: $assign$base${stmt.getInvokeExpr.getMethod.getName}(${Joiner.on(",").join(stmt.getInvokeExpr.getArgs)})"
    }

    if (stmt.isAssignStmt) {
      if (delegate.isAssignment) {
        if (stmt.isFieldStore) {
          return s"${delegate.pc}: ${stmt.getLeftOp} = ${stmt.getFieldStore.getX}.${stmt.getFieldStore.getY}"
        } else if (stmt.isArrayStore) {
          val base = stmt.getArrayBase
          return s"${delegate.pc}: ${base.getX.getVariableName}[${base.getY}] = ${stmt.getRightOp}"
        } else {
          return s"${delegate.pc}: ${stmt.getLeftOp} = ${stmt.getRightOp}"
        }
      }
    }

    if (delegate.astID == Nop.ASTID) {
      return s"${delegate.pc}: nop"
    }

    if (delegate.astID == Return.ASTID) {
      return s"${delegate.pc}: return"
    }

    if (stmt.isReturnStmt) {
      return s"${delegate.pc}: return ${stmt.getReturnOp}"
    }

    delegate.toString
  }

}
