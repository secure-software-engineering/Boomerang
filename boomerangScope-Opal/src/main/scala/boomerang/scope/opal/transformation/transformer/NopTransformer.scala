package boomerang.scope.opal.transformation.transformer

import boomerang.scope.opal.transformation.{StmtGraph, TacLocal}
import org.opalj.tac.{If, Nop, Stmt}

object NopTransformer {

  private final val INITIAL_NOP = Integer.MIN_VALUE

  def apply(stmtGraph: StmtGraph): StmtGraph = {
    val tac = stmtGraph.tac

    def addNopStatements(stmtGraph: StmtGraph): StmtGraph = {
      // Add a nop statement in the beginning
      val nop = Nop(INITIAL_NOP)
      var result = stmtGraph.insertBefore(nop, stmtGraph.heads.head)

      var beforeStmtInsert = List.empty[Stmt[TacLocal]]
      tac.zipWithIndex.foreach(stmt => {
        // TODO Switch statements
        if (stmt._1.astID == If.ASTID) {
          val nextStmt = tac(stmt._2 + 1)
          val targetStmt = tac(stmt._1.asIf.targetStmt)

          beforeStmtInsert = beforeStmtInsert ++ List(nextStmt, targetStmt)
        }
      })

      beforeStmtInsert.foreach(stmt => {
        /* If one or multiple branches target the same statement, we add exactly one Nop statement
         * before the target statement (represented by the negative pc). This way, important
         * data flows are covered
         */
        val nextStmtNop = Nop(-stmt.pc)
        if (!result.statements.contains(nextStmtNop)) {
          result = result.insertBefore(nextStmtNop, stmt)
        }
      })

      result
    }

    addNopStatements(stmtGraph)
  }
}
