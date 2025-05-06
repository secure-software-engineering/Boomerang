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

import boomerang.scope._
import boomerang.scope.opal.transformation.TacLocal
import java.util.Objects
import org.opalj.tac.Expr

class OpalArrayRef(
    val base: TacLocal,
    val indexExpr: Expr[TacLocal],
    method: OpalMethod,
    unbalanced: ControlFlowGraph.Edge = null
) extends ArrayVal(method, unbalanced) {

  override def getBase: Val = new OpalVal(base, method)

  override def getIndexExpr: Val = new OpalVal(indexExpr, method)

  override def getType: Type = OpalType.valueInformationToType(base.asVar.valueInformation, method.project)

  override def asUnbalanced(stmt: ControlFlowGraph.Edge): Val = new OpalArrayRef(base, indexExpr, method, stmt)

  override def getVariableName: String = s"$base[$indexExpr]"

  override def getIndex: Int = if (indexExpr.isIntConst) indexExpr.asIntConst.value else -1

  override def equals(other: Any): Boolean = other match {
    case that: OpalArrayRef =>
      base == that.base &&
        indexExpr == that.indexExpr
    case _ => false
  }

  override def hashCode: Int = Objects.hash(base, indexExpr)

  override def toString: String = getVariableName
}
