package boomerang.scope.opal.transformer

import org.opalj.tac.{Assignment, Stmt}

class BoomerangTACode(
                       val statements: Array[Stmt[TacLocal]],
                       val pcToIndex: Array[Int],
                       val cfg: StmtGraph
                     ) {

  def getLocals: Set[TacLocal] = statements.filter(stmt => stmt.astID == Assignment.ASTID).map(stmt => stmt.asAssignment.targetVar).toSet

  def getParameterLocals: List[TacLocal] = statements.filter(stmt => stmt.pc == -1).map(stmt => stmt.asAssignment.targetVar).toList
}
