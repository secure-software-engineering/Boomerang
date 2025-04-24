/**
 * ***************************************************************************** 
 * Copyright (c) 2025 Fraunhofer IEM, Paderborn, Germany. This program and the
 * accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at http://www.eclipse.org/legal/epl-2.0.
 *
 * <p>SPDX-License-Identifier: EPL-2.0
 *
 * <p>Contributors: Johannes Spaeth - initial API and implementation
 * *****************************************************************************
 */
package boomerang.scope.opal.transformation.stack

import org.opalj.br.PC
import scala.collection.mutable

class OperandStackHandler {

    private val pcToStack = mutable.Map.empty[PC, OperandStack]
    private val defSites = mutable.Map.empty[PC, Operand]
    private var localCounter = -1

    def getOrCreate(pc: PC): OperandStack = {
        if (pcToStack.contains(pc)) {
            pcToStack(pc).copy
        } else {
            val stack = OperandStack(this)
            pcToStack.put(pc, stack)

            stack.copy
        }
    }

    def addDefSite(pc: PC, operand: Operand): Unit = {
        defSites.put(pc, operand)
    }

    def mergeStack(nextPc: PC, incomingStack: OperandStack): Boolean = {
        val existingStack = pcToStack.getOrElse(nextPc, null)

        if (existingStack == null) {
            pcToStack.put(nextPc, incomingStack)

            true
        } else {
            var modified = false
            existingStack.stackEntries.foreach(existingOp => {
                incomingStack.stackEntries.foreach(incomingOp => {
                    if (existingOp.id == incomingOp.id) {
                        // Update the counter of the incoming operand s.t. both operands describe
                        // the same local and mark both operands as branched
                        existingOp.updateCounter(existingOp.localId)
                        incomingOp.updateCounter(existingOp.localId)

                        // TODO Merge types

                        modified = true
                    }
                })
            })
            modified
        }
    }

    def nextLocalCounter: Int = {
        localCounter += 1

        localCounter
    }

    def defSiteAtPc(pc: PC): Int = defSites
        .getOrElse(
            pc,
            throw new RuntimeException(s"No operand definition at PC $pc")
        )
        .localId

    def counterForOperand(pc: PC, id: Int, isReturn: Boolean = false): Int = {
        val stack = pcToStack.getOrElse(
            pc,
            throw new RuntimeException(s"Stack for PC $pc not available")
        )
        stack.stackEntries.foreach(op => if (op.id == id) return op.localId)

        // TODO Bug in Opal: Return always has id 0, even if it may have another id
        if (isReturn) return stack.stackEntries.head.localId

        throw new RuntimeException(s"Could not find operand with id $id on stack")
    }

    def isBranchedOperand(pc: PC, id: Int): Boolean = {
        val defSite = defSites.getOrElse(
            pc,
            throw new RuntimeException(s"No def site found at pc $pc")
        )

        defSite.isBranchedOperand
    }

}
