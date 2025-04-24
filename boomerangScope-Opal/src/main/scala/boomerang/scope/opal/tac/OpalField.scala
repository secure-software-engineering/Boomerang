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

import boomerang.scope.Field
import boomerang.scope.Type
import java.util.Objects
import org.opalj.br.FieldType
import org.opalj.br.ObjectType

class OpalField(
    declaringClass: ObjectType,
    val fieldType: FieldType,
    val name: String
) extends Field {

    override def isPredefinedField: Boolean = false

    override def isInnerClassField: Boolean = declaringClass.fqn.contains("$")

    override def getType: Type = OpalType(fieldType)

    override def hashCode: Int = Objects.hash(super.hashCode(), fieldType, name)

    override def equals(other: Any): Boolean = other match {
        case that: OpalField =>
            this.fieldType == that.fieldType && this.name == that.name
        case _ => false
    }

    override def toString: String = s"$name"
}
