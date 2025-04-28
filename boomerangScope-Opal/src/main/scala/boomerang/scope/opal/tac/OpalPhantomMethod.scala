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
import boomerang.scope.Method
import boomerang.scope.Statement
import boomerang.scope.Type
import boomerang.scope.Val
import boomerang.scope.WrappedClass
import boomerang.scope.opal.OpalFrameworkScope
import java.util
import org.opalj.br.MethodDescriptor
import org.opalj.br.MethodSignature
import org.opalj.br.ObjectType

case class OpalPhantomMethod(
    declaringClassType: ObjectType,
    name: String,
    descriptor: MethodDescriptor,
    static: Boolean
) extends Method {

  override def isStaticInitializer: Boolean =
    name == OpalFrameworkScope.STATIC_INITIALIZER

  override def isParameterLocal(value: Val): Boolean = false

  override def getParameterTypes: util.List[Type] = {
    val result = new util.ArrayList[Type]()

    descriptor.parameterTypes.foreach(paramType => {
      result.add(OpalType(paramType))
    })

    result
  }

  override def getParameterType(index: Int): Type = OpalType(
    descriptor.parameterType(index)
  )

  override def getReturnType: Type = OpalType(descriptor.returnType)

  override def isThisLocal(value: Val): Boolean = false

  override def getLocals: util.Collection[Val] = throw new RuntimeException(
    "Locals of phantom method are not available"
  )

  override def getThisLocal: Val = throw new RuntimeException(
    "this local of phantom method is not available"
  )

  override def getParameterLocals: util.List[Val] = throw new RuntimeException(
    "Parameter locals of phantom method are not available"
  )

  override def isStatic: Boolean = static

  override def isDefined: Boolean = false

  override def isPhantom: Boolean = true

  override def getStatements: util.List[Statement] = throw new RuntimeException(
    "Statements of phantom method are not available"
  )

  override def getDeclaringClass: WrappedClass = new OpalWrappedClass(declaringClassType)

  override def getControlFlowGraph: ControlFlowGraph =
    throw new RuntimeException("CFG of phantom method is not available")

  override def getSubSignature: String =
    MethodSignature(name, descriptor).toJava

  override def getName: String = name

  override def isConstructor: Boolean = name == OpalFrameworkScope.CONSTRUCTOR

  override def toString: String = s"PHANTOM: ${descriptor.toJava}"
}
