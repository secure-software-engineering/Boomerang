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

import boomerang.scope.Method
import boomerang.scope.Type
import boomerang.scope.WrappedClass
import boomerang.scope.opal.OpalClient
import java.util
import java.util.Objects
import org.opalj.br.ObjectType

class OpalWrappedClass(val delegate: ObjectType) extends WrappedClass {

  override def getMethods: util.Set[Method] = {
    val classFile = OpalClient.getClassFileForType(delegate)

    if (classFile.isDefined) {
      val methods = new util.HashSet[Method]

      classFile.get.methods.foreach(method => {
        methods.add(OpalMethod(method))
      })

      return methods
    }

    throw new RuntimeException("Class file of class not available: " + delegate.fqn)
  }

  override def hasSuperclass: Boolean = {
    val classFile = OpalClient.getClassFileForType(delegate)

    if (classFile.isDefined) {
      return classFile.get.superclassType.isDefined
    }

    false
  }

  override def getSuperclass: WrappedClass = {
    if (hasSuperclass) {
      val classFile = OpalClient.getClassFileForType(delegate)

      return new OpalWrappedClass(classFile.get.superclassType.get)
    }

    throw new RuntimeException(
      "Class " + delegate.fqn + " has no super class"
    )
  }

  override def getType: Type = OpalType(delegate)

  override def isApplicationClass: Boolean = OpalClient.project.get.classFile(delegate).isDefined

  override def getFullyQualifiedName: String = delegate.fqn.replace("/", ".")

  override def isDefined: Boolean = OpalClient.project.get.classFile(delegate).isDefined

  override def isPhantom: Boolean = OpalClient.project.get.classFile(delegate).isEmpty

  override def hashCode: Int = Objects.hash(delegate)

  override def equals(other: Any): Boolean = other match {
    case that: OpalWrappedClass => this.delegate == that.delegate
    case _ => false
  }

  override def toString: String = delegate.toString()
}
