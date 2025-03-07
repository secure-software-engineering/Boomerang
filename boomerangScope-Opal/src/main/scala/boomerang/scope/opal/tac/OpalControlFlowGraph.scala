package boomerang.scope.opal.tac

import boomerang.scope.opal.OpalClient
import boomerang.scope.{ControlFlowGraph, Statement}
import com.google.common.collect.{HashMultimap, Multimap}

import java.util

class OpalControlFlowGraph(method: OpalMethod) extends ControlFlowGraph {

  private var cacheBuilt = false

  private val startPointCache: util.List[Statement] = new util.ArrayList[Statement]
  private val endPointCache: util.List[Statement] = new util.ArrayList[Statement]
  private val predsOfCache: Multimap[Statement, Statement] = HashMultimap.create()
  private val succsOfCache: Multimap[Statement, Statement] = HashMultimap.create()
  private val statements: util.List[Statement] = new util.ArrayList[Statement]()

  override def getStartPoints: util.Collection[Statement] = {
    buildCache()
    startPointCache
  }

  override def getEndPoints: util.Collection[Statement] = {
    buildCache()
    endPointCache
  }

  override def getSuccsOf(curr: Statement): util.Collection[Statement] = {
    buildCache()
    succsOfCache.get(curr)
  }

  override def getPredsOf(curr: Statement): util.Collection[Statement] = {
    buildCache()
    predsOfCache.get(curr)
  }

  override def getStatements: util.List[Statement] = {
    buildCache()
    statements
  }

  private def buildCache(): Unit = {
    if (cacheBuilt) return

    val tac = OpalClient.getTacForMethod(method.delegate)

    // val entryPoint = tac.stmts(tac.cfg.startBlock.startPC)
    // startPointCache.add(new OpalStatement(entryPoint, method))

    tac.stmts.foreach(stmt => {
      val statement = new OpalStatement(stmt, method)
      statements.add(statement)

      val stmtPc = tac.pcToIndex(stmt.pc)

      val predecessors = tac.cfg.predecessors(stmtPc)
      if (predecessors.isEmpty) {
        // No predecessors => Head statement
        val headStatement = new OpalStatement(stmt, method)
        startPointCache.add(headStatement)
      } else {
        predecessors.foreach(predecessorPc => {
          val predecessor = tac.stmts(predecessorPc)
          val predecessorStatement = new OpalStatement(predecessor, method)

          predsOfCache.put(statement, predecessorStatement)
        })
      }

      val successors = tac.cfg.successors(stmtPc)
      if (successors.isEmpty) {
        // No successors => Tail statement
        val tailStmt = new OpalStatement(stmt, method)
        endPointCache.add(tailStmt)
      } else {
        successors.foreach(successorPc => {
          val successor = tac.stmts(successorPc)
          val successorStatement = new OpalStatement(successor, method)

          succsOfCache.put(statement, successorStatement)
        })
      }
    })

    cacheBuilt = true
  }
}
