package boomerang.scope.opal.tac

import boomerang.scope.opal.{OpalClient, OpalFrameworkScope}
import boomerang.scope.{DeclaredMethod, InvokeExpr, Type, WrappedClass}
import org.opalj.br.MethodSignature
import org.opalj.tac.{Call, DUVar}
import org.opalj.value.ValueInformation

import java.util

case class OpalDeclaredMethod(invokeExpr: InvokeExpr, delegate: Call[DUVar[ValueInformation]]) extends DeclaredMethod(invokeExpr) {

  override def getSubSignature: String = MethodSignature(delegate.name, delegate.descriptor).toJava

  override def getName: String = delegate.name

  override def isConstructor: Boolean = delegate.name == OpalFrameworkScope.CONSTRUCTOR

  override def getSignature: String = delegate.descriptor.toJava(s"${delegate.declaringClass.toJava}.${delegate.name}")

  override def getDeclaringClass: WrappedClass = {
    val decClass = OpalClient.getClassFileForType(delegate.declaringClass.asObjectType)

    if (decClass.isDefined) {
      OpalWrappedClass(decClass.get)
    } else {
      OpalPhantomWrappedClass(delegate.declaringClass)
    }
  }

  override def getParameterTypes: util.List[Type] = {
    val result = new util.ArrayList[Type]()

    delegate.descriptor.parameterTypes.foreach(paramType => {
      result.add(OpalType(paramType))
    })

    result
  }

  override def getParameterType(index: Int): Type = getParameterTypes.get(index)

  override def getReturnType: Type = OpalType(delegate.descriptor.returnType)

  override def toString: String = delegate.descriptor.toJava
}
