package boomerang.scope.opal.tac

import boomerang.scope.{ControlFlowGraph, Statement}
import com.google.common.collect.{HashMultimap, Multimap}
import org.opalj.tac.Nop

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

    // Definition of parameter locals
    method.tac.statements.filter(stmt => stmt.pc == -1).foreach(stmt => {
      val statement = new OpalStatement(stmt, method)
      statements.add(statement)
    })

    val head = new OpalStatement(Nop(-1), method)
    statements.add(head)
    startPointCache.add(head)

    var headAdded = false
    method.tac.statements.filter(stmt => stmt.pc >= 0).foreach(stmt => {
      val statement = new OpalStatement(stmt, method)
      statements.add(statement)

      val stmtPc = method.tac.pcToIndex(stmt.pc)

      if (!headAdded) {
        headAdded = true

        predsOfCache.put(statement, head)
        succsOfCache.put(head, statement)
      } else {
        val predecessors = method.tac.cfg.predecessors(stmtPc)
        predecessors.foreach(predecessorPc => {
          val predecessor = method.tac.statements(predecessorPc)
          val predecessorStatement = new OpalStatement(predecessor, method)

          predsOfCache.put(statement, predecessorStatement)
        })
      }

      val successors = method.tac.cfg.successors(stmtPc)
      if (successors.isEmpty) {
        // No successors => Tail statement
        endPointCache.add(statement)
        succsOfCache.putAll(statement, util.Collections.emptySet())
      } else {
        successors.foreach(successorPc => {
          val successor = method.tac.statements(successorPc)
          val successorStatement = new OpalStatement(successor, method)

          succsOfCache.put(statement, successorStatement)
        })
      }
    })

    cacheBuilt = true
  }
}
