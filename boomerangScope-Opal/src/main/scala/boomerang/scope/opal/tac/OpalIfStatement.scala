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

import boomerang.scope.IfStatement
import boomerang.scope.Statement
import boomerang.scope.Val
import boomerang.scope.opal.OpalClient
import boomerang.scope.opal.transformation.TacLocal
import java.util.Objects
import org.opalj.tac.DUVar
import org.opalj.tac.IdBasedVar
import org.opalj.tac.If
import org.opalj.tac.Var
import org.opalj.value.ValueInformation

class OpalIfStatement(val delegate: If[TacLocal], method: OpalMethod) extends IfStatement {

  override def getTarget: Statement = {
    /*val tac = OpalClient.getTacForMethod(method.delegate)
    val target = delegate.targetStmt

    new OpalStatement(tac.stmts(target), method)*/
    ???
  }

  override def evaluate(otherVal: Val): IfStatement.Evaluation =
    IfStatement.Evaluation.UNKNOWN

  override def uses(otherVal: Val): Boolean = {
    // TODO
    //  Only relevant for PathTrackingBoomerang that is not used and tested;
    //  has to be implemented when used
    if (otherVal.isInstanceOf[OpalVal]) {}
    if (otherVal.isInstanceOf[OpalLocal]) {}
    if (otherVal.isInstanceOf[OpalArrayRef]) {}
    val left = new OpalVal(delegate.left, method)
    val right = new OpalVal(delegate.right, method)

    otherVal.equals(left) || otherVal.equals(right)
  }

  override def hashCode: Int = Objects.hash(delegate)

  private def canEqual(a: Any): Boolean = a.isInstanceOf[OpalIfStatement]

  override def equals(obj: Any): Boolean = obj match {
    case other: OpalIfStatement =>
      other.canEqual(this) && this.delegate.pc == other.delegate.pc
    case _ => false
  }

  override def toString: String = delegate.toString()
}
