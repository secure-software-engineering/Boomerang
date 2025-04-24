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
package boomerang.scope.opal.transformation

import org.opalj.br.cfg.CFG
import org.opalj.tac.Return
import org.opalj.tac.Stmt
import org.opalj.tac.TACStmts
import scala.collection.mutable

class StmtGraph private (
    val tac: Array[Stmt[TacLocal]],
    val heads: Set[Stmt[TacLocal]],
    val tails: Set[Stmt[TacLocal]],
    val predecessors: Map[Stmt[TacLocal], Set[Stmt[TacLocal]]],
    val successors: Map[Stmt[TacLocal], Set[Stmt[TacLocal]]],
    val statements: List[Stmt[TacLocal]]
) {

    // TODO Update if and goto targets
    def insertBefore(
        insertStmt: Stmt[TacLocal],
        existingStmt: Stmt[TacLocal]
    ): StmtGraph = {
        val tempSuccessor = mutable.Map.from(successors)

        // Insert s1 between s0 -> s2
        val preds = predecessors(existingStmt)
        preds.foreach(pred => {
            // Update succs of s0 from s2 to s1, giving s0 -> s1 ... s2
            val succsOfPred = successors(pred)
            assert(succsOfPred.contains(existingStmt))

            val newSuccs = succsOfPred - existingStmt + insertStmt
            tempSuccessor.put(pred, newSuccs)
        })

        // Update preds of s2 from s0 to (only) s1, giving s0 ... s1 <- s2
        val tempPreds = mutable.Map.from(predecessors)
        tempPreds.put(existingStmt, Set(insertStmt))

        // Add the new statement
        tempPreds.put(insertStmt, preds)
        tempSuccessor.put(insertStmt, Set(existingStmt))

        // Potential head statement update
        var newHeads = heads.map(identity)
        if (heads.contains(existingStmt)) {
            newHeads = newHeads - existingStmt + insertStmt
        }

        val newStatements = statements.flatMap {
            case `existingStmt` => List(insertStmt, existingStmt)
            case x => List(x)
        }

        new StmtGraph(
            tac,
            newHeads,
            tails,
            tempPreds.toMap,
            tempSuccessor.toMap,
            newStatements
        )
    }

    def remove(stmt: Stmt[TacLocal]): StmtGraph = {
        if (tails.contains(stmt))
            throw new RuntimeException("Cannot remove tail statement")

        val tempPreds = mutable.Map.from(predecessors)
        val tempSuccs = mutable.Map.from(successors)

        val preds = predecessors(stmt)
        val succs = successors(stmt)
        preds.foreach(pred => {
            val succsOfPred = successors(pred)

            assert(succsOfPred.contains(stmt), "Inconsistent state in graph")
            tempSuccs.put(pred, succsOfPred ++ succs - stmt)
        })

        succs.foreach(succ => {
            val predsOfSuccs = predecessors(succ)

            assert(predsOfSuccs.contains(stmt), "Inconsistent state in graph")
            tempPreds.put(succ, predsOfSuccs ++ preds - stmt)
        })

        var newHeads = heads.map(identity)
        if (heads.contains(stmt)) {
            newHeads = newHeads - stmt
            newHeads = newHeads ++ succs
        }

        val newStatements = statements.filter(s => s != stmt)
        val newPreds = tempPreds.filter(s => s._1 != stmt).toMap
        val newSuccs = tempSuccs.filter(s => s._1 != stmt).toMap

        assert(!newHeads.contains(stmt))
        assert(!newStatements.contains(stmt))
        assert(!newPreds.contains(stmt))
        assert(!newSuccs.contains(stmt))
        new StmtGraph(tac, newHeads, tails, newPreds, newSuccs, newStatements)
    }
}

object StmtGraph {

    def apply(
        tac: Array[Stmt[TacLocal]],
        cfg: CFG[Stmt[TacLocal], TACStmts[TacLocal]],
        pcToIndex: Array[Int],
        exceptionHandlers: Array[Int]
    ): StmtGraph = {

        def computeHead: Set[Stmt[TacLocal]] =
            exceptionHandlers.map(eh => tac(eh)).toSet + tac(0)

        def computeTails: Set[Stmt[TacLocal]] = {
            tac
                .filter(stmt => stmt.pc >= 0)
                .filter(stmt => {
                    val stmtIndex = pcToIndex(stmt.pc)
                    val successors = cfg.successors(stmtIndex)

                    // No successors => tail statement
                    successors.isEmpty
                })
                .toSet
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
            if (exceptionHandlers.contains(stmtIndex)) {
                return Set()
            }

            val predecessors = cfg.predecessors(stmtIndex)
            predecessors.map(predecessorIndex => tac(predecessorIndex))
        }

        def computeSuccessors(stmt: Stmt[TacLocal]): Set[Stmt[TacLocal]] = {
            // Successor of identity statements is just the following statement
            if (stmt.pc < 0) {
                val stmtIndex = tac.indexOf(stmt)
                return Set(tac(stmtIndex + 1))
            }

            val stmtIndex = pcToIndex(stmt.pc)
            val successors = cfg.successors(stmtIndex)
            successors
                .filter(s => !exceptionHandlers.contains(s))
                .map(successorIndex => tac(successorIndex))
        }

        val heads = computeHead
        val tails = computeTails
        val predecessors = tac.map(stmt => stmt -> computePredecessors(stmt)).toMap
        val successors = tac.map(stmt => stmt -> computeSuccessors(stmt)).toMap

        new StmtGraph(tac, heads, tails, predecessors, successors, tac.toList)
    }
}
