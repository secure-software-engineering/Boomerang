package boomerang.scope.opal.tac

import boomerang.scope.opal.OpalClient
import boomerang.scope.{DeclaredMethod, InvokeExpr, Type, WrappedClass}
import org.opalj.br.{DefinedMethod, MethodDescriptor, MethodSignature, ReferenceType}

import java.util

case class OpalDeclaredMethod(invokeExpr: InvokeExpr, delegate: DefinedMethod) extends DeclaredMethod(invokeExpr) {

  override def isNative: Boolean = delegate.definedMethod.isNative

  override def getSubSignature: String = delegate.definedMethod.signature.toString

  override def getName: String = delegate.name

  override def isStatic: Boolean = delegate.definedMethod.isStatic

  override def isConstructor: Boolean = delegate.definedMethod.isConstructor

  override def getSignature: String = delegate.definedMethod.fullyQualifiedSignature

  override def getDeclaringClass: WrappedClass = OpalWrappedClass(delegate.definedMethod.classFile)

  override def getParameterTypes: util.List[Type] = {
    val result = new util.ArrayList[Type]()

    delegate.definedMethod.parameterTypes.foreach(paramType => {
      result.add(OpalType(paramType))
    })

    result
  }

  override def getParameterType(index: Int): Type = getParameterTypes.get(index)

  override def toString: String = getSignature
}

case class OpalPhantomDeclaredMethod(invokeExpr: InvokeExpr, declaringClass: ReferenceType, name: String, descriptor: MethodDescriptor, static: Boolean) extends DeclaredMethod(invokeExpr) {

  override def isNative: Boolean = false

  override def getSubSignature: String = MethodSignature(name, descriptor).toJava

  override def getName: String = name

  override def isStatic: Boolean = static

  override def isConstructor: Boolean = name == "<init>"

  override def getSignature: String = descriptor.toJava(s"${declaringClass.toJava}.$name")

  override def getDeclaringClass: WrappedClass = {
    val decClass = OpalClient.getClassFileForType(declaringClass.asObjectType)

    if (decClass.isDefined) {
      OpalWrappedClass(decClass.get)
    } else {
      OpalPhantomWrappedClass(declaringClass)
    }
  }

  override def getParameterTypes: util.List[Type] = {
    val result = new util.ArrayList[Type]()

    descriptor.parameterTypes.foreach(paramType => {
      result.add(OpalType(paramType))
    })

    result
  }

  override def getParameterType(index: Int): Type = getParameterTypes.get(index)

  override def toString: String = getSignature
}
