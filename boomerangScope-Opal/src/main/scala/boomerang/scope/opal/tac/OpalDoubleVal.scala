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

import boomerang.scope.Method
import boomerang.scope.Val
import boomerang.scope.ValWithFalseVariable
import boomerang.scope.opal.transformation.TacLocal
import java.util.Objects
import org.opalj.tac.Expr

class OpalDoubleVal(delegate: Expr[TacLocal], method: Method, val falseVal: Val)
    extends OpalVal(delegate, method)
    with ValWithFalseVariable {

  override def getFalseVariable: Val = falseVal

  override def hashCode: Int = Objects.hash(super.hashCode, falseVal)

  override def equals(other: Any): Boolean = other match {
    case that: OpalDoubleVal =>
      super.equals(that) && falseVal == that.falseVal

    case _ => false
  }

  override def toString: String =
    "FalseVal: " + falseVal + " from " + super.toString
}
