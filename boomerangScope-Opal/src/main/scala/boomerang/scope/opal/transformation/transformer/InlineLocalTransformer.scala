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
package boomerang.scope.opal.transformation.transformer

import boomerang.scope.opal.transformation.RegisterLocal
import boomerang.scope.opal.transformation.StackLocal
import boomerang.scope.opal.transformation.TacLocal
import boomerang.scope.opal.transformation.stack.OperandStackHandler
import org.opalj.tac._
import scala.collection.mutable

object InlineLocalTransformer {

    def apply(
        code: Array[Stmt[TacLocal]],
        stackHandler: OperandStackHandler
    ): Array[Stmt[TacLocal]] = {
        val statements = code.map(identity)

        val localCache = mutable.Map.empty[TacLocal, Expr[TacLocal]]
        val localDefSites = mutable.Map.empty[TacLocal, (Int, Int)]

        val max = code.length - 1
        Range(0, max).foreach(i => {
            statements(i) match {
                // Collect all expressions that may replace stack to register assignments
                case Assignment(pc, targetVar: StackLocal, c @ (_: SimpleValueConst)) =>
                    if (!stackHandler.isBranchedOperand(pc, targetVar.id)) {
                        if (localCache.contains(targetVar)) {
                            throw new RuntimeException("Did not discover branched operand")
                        }

                        localCache.put(targetVar, c)
                        localDefSites.put(targetVar, (i, pc))
                    }
                // Replace stack to register expressions if possible:
                // r = $s becomes r = <expr> from previous assignment $s = <expr>
                case Assignment(pc, targetVar: RegisterLocal, rightVar: StackLocal) =>
                /*if (localCache.contains(rightVar)) {
              val localExpr = localCache(rightVar)
              statements(i) = Assignment(pc, targetVar, localExpr)

              val localDefSite = localDefSites.getOrElse(rightVar, throw new RuntimeException("Def sites not consistent"))
              statements(localDefSite._1) = Nop(localDefSite._2)
            }*/
                // Array related assignments
                case Assignment(pc, targetVar: StackLocal, expr) =>
                    expr match {
                        // Replace the counts in new array creation with concrete integers if available
                        case NewArray(arrPc, counts, arrayType) =>
                            var countDefSites = List.empty[(Int, Int)]

                            val newCounts = counts.map(c => {
                                if (c.isVar && localCache.contains(c.asVar)) {
                                    val localExpr = localCache(c.asVar)

                                    if (localExpr.isIntConst) {
                                        val countDefSite = localDefSites.getOrElse(
                                            c.asVar,
                                            throw new RuntimeException("Def sites not consistent")
                                        )
                                        countDefSites = countDefSites :+ countDefSite

                                        localExpr
                                    } else {
                                        c
                                    }
                                } else {
                                    c
                                }
                            })

                            statements(i) = Assignment(pc, targetVar, NewArray(arrPc, newCounts, arrayType))
                            countDefSites.foreach(defSite => statements(defSite._1) = Nop(defSite._2))
                        // Replace the index expression with the concrete integer value if available
                        case ArrayLoad(arrPc, arrayIndex: StackLocal, arrayRef) =>
                            if (localCache.contains(arrayIndex)) {
                                val localExpr = localCache(arrayIndex)

                                if (localExpr.isIntConst) {
                                    statements(i) = Assignment(
                                        pc,
                                        targetVar,
                                        ArrayLoad(arrPc, localExpr, arrayRef)
                                    )

                                    val localDefSite = localDefSites.getOrElse(
                                        arrayIndex,
                                        throw new RuntimeException("Def sites not consistent")
                                    )
                                    statements(localDefSite._1) = Nop(localDefSite._2)
                                }
                            }
                        case _ =>
                    }
                case ArrayStore(pc, arrayRef, arrayIndex: StackLocal, value) =>
                    if (localCache.contains(arrayIndex)) {
                        val localExpr = localCache(arrayIndex)

                        if (localExpr.isIntConst) {
                            // TODO Also inline value if it is a simple constant
                            statements(i) = ArrayStore(pc, arrayRef, localExpr, value)

                            val localDefSite = localDefSites.getOrElse(
                                arrayIndex,
                                throw new RuntimeException("Def sites not consistent")
                            )
                            statements(localDefSite._1) = Nop(localDefSite._2)
                        }
                    }
                case _ =>
            }
        })

        Range(0, max).foreach(i => {
            statements(i) match {

                /* Inline simple stack to register definitions:
                 * $s = <expr>
                 * r = $s
                 *
                 * becomes
                 * r = <expr>
                 */
                case Assignment(
                        pc,
                        targetVar: StackLocal,
                        c @ (_: SimpleValueConst | _: FunctionCall[TacLocal] | _: NewArray[TacLocal] |
                        _: ArrayLoad[TacLocal] | _: GetField[TacLocal] | _: GetStatic)
                    ) =>
                    statements(i + 1) match {
                        case Assignment(
                                nextPc,
                                nextTargetVar: RegisterLocal,
                                `targetVar`
                            ) =>
                            statements(i) = Nop(pc)
                            statements(i + 1) = Assignment(nextPc, nextTargetVar, c)
                        case _ =>
                    }
                case _ =>
            }
        })

        statements
    }
}
