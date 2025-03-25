package boomerang.scope.opal.transformer

import org.opalj.br.ExceptionHandlers
import org.opalj.br.cfg.CFG
import org.opalj.tac.{Assignment, Param, Parameters, Stmt, TACStmts}

class BoomerangTACode(
                       val params: Parameters[Param],
                       val statements: Array[Stmt[TacLocal]],
                       val pcToIndex: Array[Int],
                       val cfg: CFG[Stmt[TacLocal], TACStmts[TacLocal]],
                       val exceptionHandlers: ExceptionHandlers
                     ) {

  def getLocals: Set[TacLocal] = statements.filter(stmt => stmt.astID == Assignment.ASTID).map(stmt => stmt.asAssignment.targetVar).toSet

  def getParameterLocals: List[TacLocal] = statements.filter(stmt => stmt.pc == -1).map(stmt => stmt.asAssignment.targetVar).toList
}
