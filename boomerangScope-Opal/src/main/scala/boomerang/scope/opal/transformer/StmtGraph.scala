package boomerang.scope.opal.transformer

import org.opalj.br.cfg.CFG
import org.opalj.tac.{Stmt, TACStmts}

class StmtGraph(val heads: Set[Stmt[TacLocal]], val tails: Set[Stmt[TacLocal]], val predecessors: Map[Stmt[TacLocal], Set[Stmt[TacLocal]]], val successors: Map[Stmt[TacLocal], Set[Stmt[TacLocal]]]) {

}

object StmtGraph {

  def apply(tac: Array[Stmt[TacLocal]], cfg: CFG[Stmt[TacLocal], TACStmts[TacLocal]], pcToIndex: Array[Int], offset: Int): StmtGraph = {

    def computeHeads: Set[Stmt[TacLocal]] = Set(tac(0))

    def computeTails: Set[Stmt[TacLocal]] = {
      tac.filter(stmt => stmt.pc >= 0).filter(stmt => {
        val stmtIndex = pcToIndex(stmt.pc)
        val successors = cfg.successors(stmtIndex)

        // No successors => tail statement
        successors.isEmpty
      }).toSet
    }

    def computePredecessors(stmt: Stmt[TacLocal]): Set[Stmt[TacLocal]] = {
      // head
      if (stmt == tac(0)) return Set.empty

      // Pred of identity statements are just the previous statement
      if (stmt.pc < 0) {
        val stmtIndex = tac.indexOf(stmt)
        return Set(tac(stmtIndex - 1))
      }

      // The first original statement
      if (stmt.pc == 0) {
        val stmtIndex = tac.indexOf(stmt)
        return Set(tac(stmtIndex - 1))
      }

      val stmtIndex = pcToIndex(stmt.pc)
      val predecessors = cfg.predecessors(stmtIndex)
      predecessors.map(predecessorIndex => tac(predecessorIndex + offset))
    }

    def computeSuccessors(stmt: Stmt[TacLocal]): Set[Stmt[TacLocal]] = {
      // Successor of identity statements is just the following statement
      if (stmt.pc < 0) {
        val stmtIndex = tac.indexOf(stmt)
        return Set(tac(stmtIndex + 1))
      }

      val stmtIndex = pcToIndex(stmt.pc)
      val successors = cfg.successors(stmtIndex)
      successors.map(successorIndex => tac(successorIndex + offset))
    }

    val heads = computeHeads
    val tails = computeTails
    val predecessors = tac.map(stmt => stmt -> computePredecessors(stmt)).toMap
    val successors = tac.map(stmt => stmt -> computeSuccessors(stmt)).toMap

    new StmtGraph(heads, tails, predecessors, successors)
  }
}
