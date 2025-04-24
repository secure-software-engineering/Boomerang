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
import org.opalj.br.ClassFile

case class OpalWrappedClass(delegate: ClassFile) extends WrappedClass {

    override def getMethods: util.Set[Method] = {
        val methods = new util.HashSet[Method]

        delegate.methods.foreach(method => {
            methods.add(OpalMethod(method))
        })

        methods
    }

    override def hasSuperclass: Boolean = delegate.superclassType.isDefined

    override def getSuperclass: WrappedClass = {
        if (hasSuperclass) {
            val superClass =
                OpalClient.getClassFileForType(delegate.superclassType.get)

            if (superClass.isDefined) {
                return OpalWrappedClass(superClass.get)
            } else {
                return OpalPhantomWrappedClass(delegate.superclassType.get)
            }
        }

        throw new RuntimeException(
            "Class " + delegate.thisType.toJava + " has no super class"
        )
    }

    override def getType: Type = OpalType(delegate.thisType)

    override def isApplicationClass: Boolean =
        OpalClient.isApplicationClass(delegate)

    override def getFullyQualifiedName: String = delegate.fqn.replace("/", ".")

    override def isPhantom: Boolean = false

    override def toString: String = delegate.toString()
}
