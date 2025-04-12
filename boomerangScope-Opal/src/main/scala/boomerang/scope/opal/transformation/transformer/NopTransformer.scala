package boomerang.scope.opal.transformation.transformer

import boomerang.scope.opal.transformation.StmtGraph
import org.opalj.tac.{If, Nop}

object NopTransformer {

  // TODO Make it more general
  final val INITIAL_NOP = -10

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

          result = result.insertBefore(Nop(-nextStmt.pc), nextStmt)
          result = result.insertBefore(Nop(-targetStmt.pc), targetStmt)
        }
      })

      result
    }

    // TODO Remove NOPs before inserting them
    // removeNopStatements(stmtGraph)
    addNopStatements(stmtGraph)
  }
}
