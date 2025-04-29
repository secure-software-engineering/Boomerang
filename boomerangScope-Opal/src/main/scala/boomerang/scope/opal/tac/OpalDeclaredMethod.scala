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

import boomerang.scope.DeclaredMethod
import boomerang.scope.InvokeExpr
import boomerang.scope.Type
import boomerang.scope.WrappedClass
import boomerang.scope.opal.OpalFrameworkScope
import boomerang.scope.opal.transformation.TacLocal
import boomerang.utils.MethodWrapper
import java.util
import java.util.Objects
import org.opalj.br.MethodSignature
import org.opalj.tac.Call
import scala.jdk.CollectionConverters._

case class OpalDeclaredMethod(
    invokeExpr: InvokeExpr,
    delegate: Call[TacLocal],
    method: OpalMethod
) extends DeclaredMethod(invokeExpr) {

  override def getSubSignature: String =
    MethodSignature(delegate.name, delegate.descriptor).toJava

  override def getName: String = delegate.name

  override def isConstructor: Boolean =
    delegate.name == OpalFrameworkScope.CONSTRUCTOR

  override def getSignature: String = delegate.descriptor.toJava(
    s"${delegate.declaringClass.toJava}.${delegate.name}"
  )

  override def getDeclaringClass: WrappedClass =
    new OpalWrappedClass(method.project, delegate.declaringClass.mostPreciseObjectType)

  override def getParameterTypes: util.List[Type] = {
    val result = new util.ArrayList[Type]()

    delegate.descriptor.parameterTypes.foreach(paramType => {
      result.add(OpalType(paramType, method.project.classHierarchy))
    })

    result
  }

  override def getParameterType(index: Int): Type = getParameterTypes.get(index)

  override def getReturnType: Type = OpalType(delegate.descriptor.returnType, method.project.classHierarchy)

  override def toMethodWrapper: MethodWrapper = new MethodWrapper(
    delegate.declaringClass.toJava,
    delegate.name,
    delegate.descriptor.returnType.toJava,
    delegate.descriptor.parameterTypes.map(p => p.toJava).toList.asJava
  )

  override def hashCode(): Int = Objects.hash(super.hashCode(), delegate)

  override def equals(other: Any): Boolean = other match {
    case that: OpalDeclaredMethod => super.equals(that) && this.delegate == that.delegate
    case _ => false
  }

  override def toString: String = delegate.descriptor.toJava
}
