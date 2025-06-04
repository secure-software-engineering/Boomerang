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
import boomerang.scope.opal.transformation.TacLocal
import java.util.Objects
import org.opalj.br.ArrayType
import org.opalj.br.DoubleType
import org.opalj.br.FloatType
import org.opalj.br.IntegerType
import org.opalj.br.LongType
import org.opalj.br.ObjectType
import org.opalj.br.analyses.Project
import org.opalj.tac.ArrayLength
import org.opalj.tac.ArrayLoad
import org.opalj.tac.BinaryExpr
import org.opalj.tac.ClassConst
import org.opalj.tac.Compare
import org.opalj.tac.DoubleConst
import org.opalj.tac.DynamicConst
import org.opalj.tac.Expr
import org.opalj.tac.FloatConst
import org.opalj.tac.GetField
import org.opalj.tac.GetStatic
import org.opalj.tac.InstanceOf
import org.opalj.tac.IntConst
import org.opalj.tac.InvokedynamicFunctionCall
import org.opalj.tac.LongConst
import org.opalj.tac.MethodHandleConst
import org.opalj.tac.MethodTypeConst
import org.opalj.tac.New
import org.opalj.tac.NewArray
import org.opalj.tac.NonVirtualFunctionCall
import org.opalj.tac.NullExpr
import org.opalj.tac.PrefixExpr
import org.opalj.tac.PrimitiveTypecastExpr
import org.opalj.tac.StaticFunctionCall
import org.opalj.tac.StringConst
import org.opalj.tac.VirtualFunctionCall
import org.opalj.value.ValueInformation

class OpalType(val delegate: org.opalj.br.Type, project: Project[_]) extends Type {

  override def isNullType: Boolean = false

  override def isRefType: Boolean = delegate.isObjectType

  override def isArrayType: Boolean = delegate.isArrayType

  override def getArrayBaseType: Type = new OpalType(delegate.asArrayType.componentType, project)

  override def getWrappedClass: WrappedClass = {
    if (isRefType) {
      return new OpalWrappedClass(delegate.asReferenceType.mostPreciseObjectType, project)
    }

    throw new RuntimeException("Class of non reference type not available")
  }

  override def doesCastFail(targetValType: Type, target: Val): Boolean = {
    if (!isRefType || !targetValType.isRefType) {
      return false
    }

    false
  }

  override def isSubtypeOf(otherType: String): Boolean = {
    if (!delegate.isObjectType) {
      return false
    }

    project.classHierarchy.isSubtypeOf(
      delegate.asObjectType,
      ObjectType(otherType.replace(".", "/"))
    )
  }

  override def isSupertypeOf(subType: String): Boolean = {
    if (!delegate.isObjectType) {
      return false
    }

    project.classHierarchy.isSubtypeOf(
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

  def getTypeForExpr(expr: Expr[_], project: Project[_]): Type = {
    expr match {
      case instanceOf: InstanceOf[_] => return new OpalType(instanceOf.cmpTpe, project)
      case _: Compare[_] => return new OpalType(IntegerType, project)
      case methodTypeConst: MethodTypeConst => return new OpalType(methodTypeConst.value.returnType, project)
      case methodHandleConst: MethodHandleConst =>
        return new OpalType(methodHandleConst.value.runtimeValueType, project)
      case _: IntConst => return new OpalType(IntegerType, project)
      case _: LongConst => return new OpalType(LongType, project)
      case _: FloatConst => return new OpalType(FloatType, project)
      case _: DoubleConst => return new OpalType(DoubleType, project)
      case _: StringConst => return new OpalType(ObjectType.String, project)
      case classConst: ClassConst => return new OpalType(classConst.value, project)
      case dynamicConst: DynamicConst => return new OpalType(dynamicConst.descriptor, project)
      case nullExpr: NullExpr => return new OpalType(nullExpr.tpe, project)
      case _: BinaryExpr[_] => return new OpalType(IntegerType, project)
      case _: PrefixExpr[_] => return new OpalType(IntegerType, project)
      case typeCast: PrimitiveTypecastExpr[_] => return new OpalType(typeCast.targetTpe, project)
      case newExpr: New => return new OpalType(newExpr.tpe, project)
      case newArray: NewArray[_] => return new OpalType(newArray.tpe, project)
      case arrayLoad: ArrayLoad[_] =>
        val arrayType = getTypeForExpr(arrayLoad.arrayRef, project).asInstanceOf[OpalType]
        return new OpalType(ArrayType(arrayType.delegate.asFieldType), project)
      case _: ArrayLength[_] => return new OpalType(IntegerType, project)
      case getField: GetField[_] => return new OpalType(getField.declaredFieldType, project)
      case getStatic: GetStatic => return new OpalType(getStatic.declaredFieldType, project)
      case invokeDynamic: InvokedynamicFunctionCall[_] =>
        return new OpalType(invokeDynamic.descriptor.returnType, project)
      case functionCall: NonVirtualFunctionCall[_] => return new OpalType(functionCall.descriptor.returnType, project)
      case functionCall: VirtualFunctionCall[_] => return new OpalType(functionCall.descriptor.returnType, project)
      case functionCall: StaticFunctionCall[_] => return new OpalType(functionCall.descriptor.returnType, project)
      case v: TacLocal => return valueInformationToType(v.valueInformation, project)
      case _ => throw new RuntimeException("Unknown expression: " + expr)
    }

    throw new RuntimeException("Cannot compute type for expression: " + expr)
  }

  def valueInformationToType(value: ValueInformation, project: Project[_]): Type = {
    if (value.isIllegalValue) {
      return new OpalType(ObjectType.Void, project)
    }

    if (value.isPrimitiveValue) {
      return new OpalType(value.asPrimitiveValue.primitiveType, project)
    }

    if (value.isReferenceValue) {
      if (value.asReferenceValue.isPrecise) {
        if (value.asReferenceValue.isNull.isYes) {
          return OpalNullType
        } else {
          return new OpalType(value.asReferenceValue.asReferenceType, project)
        }
      } else {
        return new OpalType(value.asReferenceValue.upperTypeBound.head, project)
      }
    }

    if (value.isVoid) {
      return new OpalType(ObjectType.Void, project)
    }

    // TODO Array and illegal types (not sure if they ever occur)
    throw new RuntimeException("Type not implemented yet")
  }
}
