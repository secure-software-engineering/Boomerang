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

import boomerang.scope.ControlFlowGraph
import boomerang.scope.Field
import boomerang.scope.Method
import boomerang.scope.StaticFieldVal
import boomerang.scope.Type
import boomerang.scope.Val
import boomerang.scope.WrappedClass
import java.util.Objects
import org.opalj.br.FieldType
import org.opalj.br.ObjectType

class OpalStaticFieldRef(
    val declaringClass: ObjectType,
    val fieldType: FieldType,
    val name: String,
    method: Method,
    unbalanced: ControlFlowGraph.Edge = null
) extends StaticFieldVal(method, unbalanced) {

  override def getDeclaringClass: WrappedClass = new OpalWrappedClass(declaringClass)

  override def getField: Field = new OpalField(declaringClass, fieldType, name)

  override def getType: Type = OpalType(fieldType)

  override def asUnbalanced(stmt: ControlFlowGraph.Edge): Val =
    new OpalStaticFieldRef(declaringClass, fieldType, name, method, stmt)

  override def withNewMethod(callee: Method): Val = new OpalStaticFieldRef(declaringClass, fieldType, name, callee)

  override def getVariableName: String = s"${declaringClass.fqn}.$name"

  override def hashCode: Int = Objects.hash(super.hashCode(), declaringClass, fieldType, name)

  override def equals(other: Any): Boolean = other match {
    case that: OpalStaticFieldRef =>
      super.equals(
        that
      ) && this.declaringClass == that.declaringClass && this.fieldType == that.fieldType && this.name == that.name
    case _ => false
  }

  override def toString: String = getVariableName
}
