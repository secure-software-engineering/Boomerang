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
import org.opalj.br.FieldType
import org.opalj.br.ObjectType
import org.opalj.tac.Expr

class OpalInstanceFieldRef(
    val objRef: Expr[TacLocal],
    val declaringClass: ObjectType,
    val fieldType: FieldType,
    val fieldName: String,
    method: OpalMethod,
    unbalanced: ControlFlowGraph.Edge = null
) extends InstanceFieldVal(method, unbalanced) {

  override def getBase: Val = new OpalVal(objRef, method)

  override def getField: Field = new OpalField(method.project, declaringClass, fieldType, fieldName)

  override def getType: Type = OpalType(fieldType, method.project.classHierarchy)

  override def asUnbalanced(stmt: ControlFlowGraph.Edge): Val =
    new OpalInstanceFieldRef(objRef, declaringClass, fieldType, fieldName, method, stmt)

  override def getVariableName: String = s"$objRef.$fieldName"

  override def hashCode: Int = Objects.hash(super.hashCode(), objRef, declaringClass, fieldType, fieldName)

  override def equals(other: Any): Boolean = other match {
    case that: OpalInstanceFieldRef =>
      super.equals(
        that
      ) && this.objRef == that.objRef && this.declaringClass == that.declaringClass && this.fieldType == that.fieldType && this.fieldName == that.fieldName
    case _ => false
  }

  override def toString: String = getVariableName
}
