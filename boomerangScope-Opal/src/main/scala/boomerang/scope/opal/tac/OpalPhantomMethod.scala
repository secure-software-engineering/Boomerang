package boomerang.scope.opal.tac

import boomerang.scope.opal.OpalFrameworkScope
import boomerang.scope.{ControlFlowGraph, Method, Statement, Type, Val, WrappedClass}
import org.opalj.br.{MethodDescriptor, MethodSignature, ObjectType, VirtualDeclaredMethod}

import java.util

case class OpalPhantomMethod(declaringClassType: ObjectType, name: String, descriptor: MethodDescriptor, static: Boolean) extends Method {

  override def isStaticInitializer: Boolean = name == OpalFrameworkScope.STATIC_INITIALIZER

  override def isParameterLocal(value: Val): Boolean = false

  override def getParameterTypes: util.List[Type] = {
    val result = new util.ArrayList[Type]()

    descriptor.parameterTypes.foreach(paramType => {
      result.add(OpalType(paramType))
    })

    result
  }

  override def getParameterType(index: Int): Type = OpalType(descriptor.parameterType(index))

  override def getReturnType: Type = OpalType(descriptor.returnType)

  override def isThisLocal(value: Val): Boolean = false

  override def getLocals: util.Collection[Val] = throw new RuntimeException("Locals of phantom method are not available")

  override def getThisLocal: Val = throw new RuntimeException("this local of phantom method is not available")

  override def getParameterLocals: util.List[Val] = throw new RuntimeException("Parameter locals of phantom method are not available")

  override def isStatic: Boolean = static

  override def isDefined: Boolean = false

  override def isPhantom: Boolean = true

  override def getStatements: util.List[Statement] = throw new RuntimeException("Statements of phantom method are not available")

  // TODO
  override def getDeclaringClass: WrappedClass = OpalPhantomWrappedClass(declaringClassType)

  override def getControlFlowGraph: ControlFlowGraph = throw new RuntimeException("CFG of phantom method is not available")

  override def getSubSignature: String = MethodSignature(name, descriptor).toJava

  override def getName: String = name

  override def isConstructor: Boolean = name == OpalFrameworkScope.CONSTRUCTOR

  override def toString: String = s"PHANTOM: ${descriptor.toJava}"
}
