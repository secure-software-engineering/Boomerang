package boomerang.scope.opal.tac

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

  def get(): OpalControlFlowGraph = {
    buildCache()

    this
  }

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

    var headFound = false
    method.tacCode.stmts.foreach(stmt => {
      // Definition of parameter locals have implicit PC of -1, so they are not part of the actual CFG
      if (stmt.pc != -1) {
        val statement = new OpalStatement(stmt, method)
        statements.add(statement)

        val stmtPc = method.tacCode.pcToIndex(stmt.pc)

        // The first statement after the parameter local definitions is the head
        if (!headFound) {
          startPointCache.add(statement)
          headFound = true
        } else {
          val predecessors = method.tacCode.cfg.predecessors(stmtPc)
          predecessors.foreach(predecessorPc => {
            val predecessor = method.tacCode.stmts(predecessorPc)
            val predecessorStatement = new OpalStatement(predecessor, method)

            predsOfCache.put(statement, predecessorStatement)
          })
        }

        val successors = method.tacCode.cfg.successors(stmtPc)
        if (successors.isEmpty) {
          // No successors => Tail statement
          val tailStmt = new OpalStatement(stmt, method)
          endPointCache.add(tailStmt)
        } else {
          successors.foreach(successorPc => {
            val successor = method.tacCode.stmts(successorPc)
            val successorStatement = new OpalStatement(successor, method)

            succsOfCache.put(statement, successorStatement)
          })
        }
      }
    })

    cacheBuilt = true
  }
}
