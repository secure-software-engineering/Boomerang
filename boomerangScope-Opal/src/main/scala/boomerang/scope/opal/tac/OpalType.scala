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
package boomerang.scope.opal.tac

import boomerang.scope.AllocVal
import boomerang.scope.Type
import boomerang.scope.Val
import boomerang.scope.WrappedClass
import boomerang.scope.opal.OpalClient
import org.opalj.br.ObjectType

case class OpalType(delegate: org.opalj.br.Type) extends Type {

    override def isNullType: Boolean = false

    override def isRefType: Boolean = delegate.isObjectType

    override def isArrayType: Boolean = delegate.isArrayType

    override def getArrayBaseType: Type = OpalType(delegate.asArrayType)

    override def getWrappedClass: WrappedClass = {
        if (isRefType) {
            val declaringClass = OpalClient.getClassFileForType(delegate.asObjectType)

            if (declaringClass.isDefined) {
                OpalWrappedClass(declaringClass.get)
            } else {
                OpalPhantomWrappedClass(delegate.asReferenceType)
            }
        }

        throw new RuntimeException(
            "Cannot compute declaring class because type is not a RefType"
        )
    }

    override def doesCastFail(targetValType: Type, target: Val): Boolean = {
        if (!isRefType || !targetValType.isRefType) {
            return false
        }

        val sourceType = delegate.asReferenceType
        val targetType =
            targetValType.asInstanceOf[OpalType].delegate.asReferenceType

        false

        /*target match {
      case allocVal: AllocVal if allocVal.getAllocVal.isNewExpr => OpalClient.getClassHierarchy.isSubtypeOf(sourceType, targetType)

      case _ => OpalClient.getClassHierarchy.isSubtypeOf(sourceType, targetType) || OpalClient.getClassHierarchy.isSubtypeOf(targetType, sourceType)
    }*/
    }

    override def isSubtypeOf(otherType: String): Boolean = {
        if (!delegate.isObjectType) {
            return false
        }

        OpalClient.getClassHierarchy.isSubtypeOf(
            delegate.asObjectType,
            ObjectType(otherType.replace(".", "/"))
        )
    }

    override def isSupertypeOf(subType: String): Boolean = {
        if (!delegate.isObjectType) {
            return false
        }

        OpalClient.getClassHierarchy.isSubtypeOf(
            ObjectType(subType),
            delegate.asObjectType
        )
    }

    override def isBooleanType: Boolean = delegate.isBooleanType

    override def toString: String = delegate.toJava
}
