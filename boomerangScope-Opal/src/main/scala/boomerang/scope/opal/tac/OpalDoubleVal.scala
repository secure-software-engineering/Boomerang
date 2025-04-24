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

import boomerang.scope.Val
import boomerang.scope.ValWithFalseVariable
import boomerang.scope.opal.transformation.TacLocal
import java.util.Objects
import org.opalj.tac.DUVar
import org.opalj.tac.Expr
import org.opalj.tac.IdBasedVar
import org.opalj.tac.Var
import org.opalj.value.ValueInformation

class OpalDoubleVal(delegate: Expr[TacLocal], method: OpalMethod, falseVal: Val)
    extends OpalVal(delegate, method)
    with ValWithFalseVariable {

    override def getFalseVariable: Val = falseVal

    override def hashCode: Int = Objects.hash(super.hashCode, falseVal)

    private def canEqual(a: Any): Boolean = a.isInstanceOf[OpalDoubleVal]

    override def equals(obj: Any): Boolean = obj match {
        case other: OpalDoubleVal =>
            other.canEqual(this) && super.equals(other) && falseVal.equals(
                other.getFalseVariable
            )
        case _ => false
    }

    override def toString: String =
        "FalseVal: " + falseVal + " from " + super.toString
}
