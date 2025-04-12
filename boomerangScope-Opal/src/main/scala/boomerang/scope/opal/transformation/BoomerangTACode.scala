package boomerang.scope.opal.transformation

import org.opalj.tac.{Assignment, Stmt}

class BoomerangTACode(val cfg: StmtGraph) {

  def statements: Array[Stmt[TacLocal]] = cfg.statements.toArray

  def getLocals: Set[TacLocal] = statements.filter(stmt => stmt.astID == Assignment.ASTID).map(stmt => stmt.asAssignment.targetVar).toSet

  def getParameterLocals: List[TacLocal] = statements.filter(stmt => stmt.pc == -1).map(stmt => stmt.asAssignment.targetVar).toList
}
