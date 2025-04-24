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
import org.opalj.br.ReferenceType

case class OpalPhantomWrappedClass(delegate: ReferenceType) extends WrappedClass {

    override def getMethods: util.Set[Method] = throw new RuntimeException(
        "Methods of class " + delegate.toString + " are not available"
    )

    override def hasSuperclass: Boolean = false

    override def getSuperclass: WrappedClass = throw new RuntimeException(
        "Super class of " + delegate.toString + " is not available"
    )

    override def getType: Type = OpalType(delegate)

    override def isApplicationClass: Boolean = false

    override def getFullyQualifiedName: String = delegate.toJava

    override def isPhantom: Boolean = true

    override def toString: String = delegate.toString
}
