package boomerang.scope.opal.tac

import boomerang.scope.opal.OpalClient
import boomerang.scope.{AllocVal, Type, Val, WrappedClass}
import org.opalj.br.ObjectType

case class OpalType(delegate: org.opalj.br.Type, isNull: Boolean = false) extends Type {

  override def isNullType: Boolean = isNull

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

    throw new RuntimeException("Cannot compute declaring class because type is not a RefType")
  }

  override def doesCastFail(targetValType: Type, target: Val): Boolean = {
    if (!isRefType || !targetValType.isRefType) {
      return false
    }

    val sourceType = delegate.asReferenceType
    val targetType = targetValType.asInstanceOf[OpalType].delegate.asReferenceType

    target match {
      case allocVal: AllocVal if allocVal.getAllocVal.isNewExpr => OpalClient.getClassHierarchy.isSubtypeOf(sourceType, targetType)

      case _ => OpalClient.getClassHierarchy.isSubtypeOf(sourceType, targetType)
    }
  }

  override def isSubtypeOf(otherType: String): Boolean = {
    if (!delegate.isObjectType) {
      return false
    }

    OpalClient.getClassHierarchy.isSubtypeOf(delegate.asObjectType, ObjectType(otherType.replace(".", "/")))
  }

  override def isSupertypeOf(subType: String): Boolean = {
    if (!delegate.isObjectType) {
      return false
    }

    OpalClient.getClassHierarchy.isSubtypeOf(ObjectType(subType), delegate.asObjectType)
  }

  override def isBooleanType: Boolean = delegate.isBooleanType

  override def toString: String = delegate.toJava
}
