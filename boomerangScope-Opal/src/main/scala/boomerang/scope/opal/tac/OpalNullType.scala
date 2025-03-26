package boomerang.scope.opal.tac

import boomerang.scope.{Type, Val, WrappedClass}

object OpalNullType extends Type {

  override def isNullType: Boolean = true

  override def isRefType: Boolean = false

  override def isArrayType: Boolean = false

  override def getArrayBaseType: Type = throw new RuntimeException("Null type has no array base type")

  override def getWrappedClass: WrappedClass = throw new RuntimeException("Null type has no declaring class")

  override def doesCastFail(targetVal: Type, target: Val): Boolean = true

  override def isSubtypeOf(superType: String): Boolean = false

  override def isSupertypeOf(subType: String): Boolean = false

  override def isBooleanType: Boolean = false
}
