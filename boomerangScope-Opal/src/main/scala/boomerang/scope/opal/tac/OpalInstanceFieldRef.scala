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

class OpalInstanceFieldRef(
    val base: Val,
    val field: Field,
    method: Method,
    unbalanced: ControlFlowGraph.Edge = null
) extends InstanceFieldVal(method, unbalanced) {

  override def getBase: Val = base

  override def getField: Field = field

  override def getType: Type = field.getType

  override def asUnbalanced(stmt: ControlFlowGraph.Edge): Val = new OpalInstanceFieldRef(base, field, method, stmt)

  override def withNewMethod(callee: Method): Val = new OpalInstanceFieldRef(base, field, callee)

  override def getVariableName: String = s"$base.${field.getName}"

  override def hashCode: Int = Objects.hash(base, field)

  override def equals(other: Any): Boolean = other match {
    case that: OpalInstanceFieldRef => super.equals(that) && this.base == that.base && this.field == that.field
    case _ => false
  }

  override def toString: String = getVariableName
}
