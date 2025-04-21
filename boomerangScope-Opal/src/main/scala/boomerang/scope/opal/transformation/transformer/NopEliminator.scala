package boomerang.scope.opal.transformation.transformer

import boomerang.scope.opal.transformation.StmtGraph
import org.opalj.tac.Nop

object NopEliminator {

  def apply(stmtGraph: StmtGraph): StmtGraph = {

    def removeNopStatements(stmtGraph: StmtGraph): StmtGraph = {
      val nopStatements = stmtGraph.statements.filter(s => s.astID == Nop.ASTID && s.pc >= 0)
      var result = stmtGraph

      nopStatements.foreach(stmt => result = result.remove(stmt))
      result
    }

    removeNopStatements(stmtGraph)
  }
}
