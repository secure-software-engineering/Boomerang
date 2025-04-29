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

import boomerang.scope.PhantomMethod
import boomerang.scope.Type
import boomerang.scope.WrappedClass
import boomerang.scope.opal.OpalFrameworkScope
import java.util
import java.util.Objects
import org.opalj.br.MethodDescriptor
import org.opalj.br.MethodSignature
import org.opalj.br.ObjectType
import org.opalj.br.analyses.Project

class OpalPhantomMethod(
    val declaringClassType: ObjectType,
    val name: String,
    val descriptor: MethodDescriptor,
    val static: Boolean,
    project: Project[_]
) extends PhantomMethod {

  override def isStaticInitializer: Boolean =
    name == OpalFrameworkScope.STATIC_INITIALIZER

  override def getParameterTypes: util.List[Type] = {
    val result = new util.ArrayList[Type]()

    descriptor.parameterTypes.foreach(paramType => {
      result.add(new OpalType(paramType, project))
    })

    result
  }

  override def getParameterType(index: Int): Type = new OpalType(
    descriptor.parameterType(index),
    project
  )

  override def getReturnType: Type = new OpalType(descriptor.returnType, project)

  override def isStatic: Boolean = static

  override def getDeclaringClass: WrappedClass = new OpalWrappedClass(declaringClassType, project)

  override def getSubSignature: String =
    MethodSignature(name, descriptor).toJava

  override def getName: String = name

  override def isConstructor: Boolean = name == OpalFrameworkScope.CONSTRUCTOR

  override def hashCode: Int = Objects.hash(declaringClassType, name, descriptor, static)

  override def equals(other: Any): Boolean = other match {
    case that: OpalPhantomMethod =>
      this.declaringClassType == that.declaringClassType && this.name == that.name && this.descriptor == that.descriptor && this.static == that.static
    case _ => false
  }

  override def toString: String = s"PHANTOM: ${declaringClassType.toJava} $name"
}
