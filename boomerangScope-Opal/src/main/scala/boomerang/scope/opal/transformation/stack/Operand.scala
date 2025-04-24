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

import java.util.Objects
import org.opalj.br.ComputationalType

class Operand(
    val id: Int,
    val cTpe: ComputationalType,
    private var counter: Int
) {

    private var modified = false

    def localId: Int = counter

    def updateCounter(newCount: Int): Unit = {
        counter = newCount
        modified = true
    }

    def isBranchedOperand: Boolean = modified

    override def hashCode: Int = Objects.hash(id)

    override def equals(obj: Any): Boolean = obj match {
        case that: Operand => this.id == that.id
        case _ => false
    }

    override def toString: String = s"op$id ($counter)"
}
