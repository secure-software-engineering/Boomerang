package boomerang.scope.opal.transformation.transformer

import boomerang.scope.opal.transformation.StmtGraph
import org.opalj.tac.{If, Nop}

object NopTransformer {

  private final val INITIAL_NOP = Integer.MIN_VALUE

  def apply(stmtGraph: StmtGraph): StmtGraph = {
    val tac = stmtGraph.tac

    def removeNopStatements(stmtGraph: StmtGraph): Unit = {
      tac.foreach(stmt => {
        if (stmt.astID == Nop.ASTID) {
          stmtGraph.remove(stmt)
        }
      })
    }

    def addNopStatements(stmtGraph: StmtGraph): StmtGraph = {
      // Add a nop statement in the beginning
      val nop = Nop(INITIAL_NOP)
      var result = stmtGraph.insertBefore(nop, stmtGraph.heads.head)

      tac.zipWithIndex.foreach(stmt => {
        if (stmt._1.astID == If.ASTID) {
          val nextStmt = tac(stmt._2 + 1)
          val targetStmt = tac(stmt._1.asIf.targetStmt)

          /* If one or multiple branches target the same statement, we add exactly one Nop statement
           * before the target statement (represented by the negative pc). This way, important
           * data flows are covered
           */
          val nextStmtNop = Nop(-nextStmt.pc)
          if (!result.statements.contains(nextStmtNop)) {
            result = result.insertBefore(nextStmtNop, nextStmt)
          }

          val targetStmtNop = Nop(-targetStmt.pc)
          if (!result.statements.contains(targetStmtNop)) {
            result = result.insertBefore(targetStmtNop, targetStmt)
          }
        }
      })

      result
    }

    // TODO Remove NOPs before inserting them
    // removeNopStatements(stmtGraph)
    addNopStatements(stmtGraph)
  }
}
