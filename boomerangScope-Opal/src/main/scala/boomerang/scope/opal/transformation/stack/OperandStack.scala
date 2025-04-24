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
package boomerang.scope.opal.transformation.stack

import org.opalj.tac.IdBasedVar

class OperandStack private (
    stackHandler: OperandStackHandler,
    private var stack: List[Operand]
) {

    def stackEntries: List[Operand] = stack

    def push(idBasedVar: IdBasedVar): Unit = {
        val operand =
            new Operand(idBasedVar.id, idBasedVar.cTpe, stackHandler.nextLocalCounter)
        stack = operand :: stack
    }

    def push(operand: Operand): Unit = {
        stack = operand :: stack
    }

    def pop(idBasedVar: IdBasedVar): Unit = {
        if (stack.isEmpty) {
            throw new IllegalStateException(
                s"Cannot pop operand $idBasedVar from empty stack"
            )
        }

        // Check if stack is in consistent state
        val top :: rest = stack
        assert(
            idBasedVar.id == top.id,
            s"Invalid pop operation on operand $idBasedVar"
        )

        // Update stack
        stack = rest
    }

    def pop: Operand = {
        if (stack.isEmpty) {
            throw new IllegalStateException(s"Cannot pop operand from empty stack")
        }

        val top :: rest = stack
        stack = rest

        top
    }

    def peek: Operand = {
        if (stack.isEmpty) return null

        val top :: _ = stack
        top
    }

    def copy: OperandStack = new OperandStack(stackHandler, stack.map(identity))

    override def toString: String = stack.toString()
}

object OperandStack {

    def apply(
        stackHandler: OperandStackHandler,
        stack: List[Operand] = List.empty
    ) = new OperandStack(stackHandler, stack)
}
