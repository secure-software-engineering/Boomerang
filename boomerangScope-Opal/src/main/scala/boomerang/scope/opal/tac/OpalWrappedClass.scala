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
import java.util
import java.util.Objects
import org.opalj.br.ObjectType
import org.opalj.br.analyses.Project

class OpalWrappedClass(val project: Project[_], val delegate: ObjectType) extends WrappedClass {

  override def getMethods: util.Set[Method] = {
    val classFile = project.classFile(delegate)

    if (classFile.isDefined) {
      val methods = new util.HashSet[Method]

      classFile.get.methods.foreach(method => {
        methods.add(OpalMethod(project, method))
      })

      return methods
    }

    throw new RuntimeException("Class file of class not available: " + delegate.fqn)
  }

  override def hasSuperclass: Boolean = {
    val classFile = project.classFile(delegate)

    if (classFile.isDefined) {
      return classFile.get.superclassType.isDefined
    }

    false
  }

  override def getSuperclass: WrappedClass = {
    if (hasSuperclass) {
      val classFile = project.classFile(delegate).get

      return new OpalWrappedClass(project, classFile.superclassType.get)
    }

    throw new RuntimeException(
      "Class " + delegate.fqn + " has no super class"
    )
  }

  override def getType: Type = OpalType(delegate, project.classHierarchy)

  override def isApplicationClass: Boolean = project.isProjectType(delegate)

  override def getFullyQualifiedName: String = delegate.fqn.replace("/", ".")

  override def isDefined: Boolean = project.classFile(delegate).isDefined

  override def isPhantom: Boolean = project.classFile(delegate).isEmpty

  override def hashCode: Int = Objects.hash(delegate)

  override def equals(other: Any): Boolean = other match {
    case that: OpalWrappedClass => this.delegate == that.delegate
    case _ => false
  }

  override def toString: String = delegate.toString()
}
