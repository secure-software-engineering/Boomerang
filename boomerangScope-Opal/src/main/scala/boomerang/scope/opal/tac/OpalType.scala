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

import boomerang.scope.Type
import boomerang.scope.Val
import boomerang.scope.WrappedClass
import boomerang.scope.opal.OpalClient
import java.util.Objects
import org.opalj.br.FieldType
import org.opalj.br.ObjectType
import org.opalj.value.ValueInformation

class OpalType(val delegate: org.opalj.br.Type) extends Type {

  override def isNullType: Boolean = false

  override def isRefType: Boolean = delegate.isObjectType

  override def isArrayType: Boolean = delegate.isArrayType

  override def getArrayBaseType: Type = new OpalType(delegate.asArrayType.componentType)

  override def getWrappedClass: WrappedClass = {
    if (isRefType) {
      return new OpalWrappedClass(delegate.asReferenceType.mostPreciseObjectType)
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
      ObjectType(subType.replace(".", "/")),
      delegate.asObjectType
    )
  }

  override def isBooleanType: Boolean = delegate.isBooleanType

  override def hashCode: Int = Objects.hash(delegate)

  override def equals(other: Any): Boolean = other match {
    case that: OpalType => this.delegate == that.delegate
    case _ => false
  }

  override def toString: String = delegate.toJava
}

object OpalType {

  def apply(fieldType: org.opalj.br.Type): Type = new OpalType(fieldType)

  def apply(value: ValueInformation): Type = {
    if (value.isPrimitiveValue) {
      return new OpalType(value.asPrimitiveValue.primitiveType)
    }

    if (value.isReferenceValue) {
      if (value.asReferenceValue.isPrecise) {
        if (value.asReferenceValue.isNull.isYes) {
          return OpalNullType
        } else {
          return new OpalType(value.asReferenceValue.asReferenceType)
        }
      } else {
        return new OpalType(value.asReferenceValue.upperTypeBound.head)
      }
    }

    if (value.isVoid) {
      return new OpalType(ObjectType.Void)
    }

    // TODO Array and illegal types (not sure if they ever occur)
    throw new RuntimeException("Type not implemented yet")
  }
}
