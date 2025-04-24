/**
 * ***************************************************************************** 
 * Copyright (c) 2018 Fraunhofer IEM, Paderborn, Germany
 * <p>
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * <p>
 * SPDX-License-Identifier: EPL-2.0
 * <p>
 * Contributors:
 *   Johannes Spaeth - initial API and implementation
 * *****************************************************************************
 */
package boomerang.scope.opal.tac

import boomerang.scope.ControlFlowGraph
import boomerang.scope.Statement
import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import java.util
import org.opalj.tac.Nop

class OpalControlFlowGraph(method: OpalMethod) extends ControlFlowGraph {

  private var cacheBuilt = false

  private val startPointCache: util.List[Statement] =
    new util.ArrayList[Statement]
  private val endPointCache: util.List[Statement] =
    new util.ArrayList[Statement]
  private val predsOfCache: Multimap[Statement, Statement] =
    HashMultimap.create()
  private val succsOfCache: Multimap[Statement, Statement] =
    HashMultimap.create()
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

    val graph = method.tac.cfg

    graph.heads.foreach(stmt => {
      val headStmt = new OpalStatement(stmt, method)

      startPointCache.add(headStmt)
    })

    graph.tails.foreach(stmt => {
      val tailStmt = new OpalStatement(stmt, method)

      endPointCache.add(tailStmt)
    })

    method.tac.statements.foreach(stmt => {
      val statement = new OpalStatement(stmt, method)
      statements.add(statement)

      graph
        .predecessors(stmt)
        .foreach(pred => {
          val predStmt = new OpalStatement(pred, method)
          predsOfCache.put(statement, predStmt)
        })

      graph
        .successors(stmt)
        .foreach(succ => {
          val succStmt = new OpalStatement(succ, method)
          succsOfCache.put(statement, succStmt)
        })
    })

    cacheBuilt = true
  }
}
