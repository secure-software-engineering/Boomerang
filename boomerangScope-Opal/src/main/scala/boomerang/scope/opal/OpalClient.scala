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
package boomerang.scope.opal

import org.opalj.br.ClassFile
import org.opalj.br.ClassHierarchy
import org.opalj.br.DefinedMethod
import org.opalj.br.Field
import org.opalj.br.Method
import org.opalj.br.MethodDescriptor
import org.opalj.br.ObjectType
import org.opalj.br.ReferenceType
import org.opalj.br.analyses.DeclaredMethods
import org.opalj.br.analyses.DeclaredMethodsKey
import org.opalj.br.analyses.Project

object OpalClient {

    var project: Option[Project[_]] = None

    def init(p: Project[_]): Unit = {
        project = Some(p)
    }

    def getClassHierarchy: ClassHierarchy = project.get.classHierarchy

    def getClassFileForType(objectType: ObjectType): Option[ClassFile] =
        project.get.classFile(objectType)

    def isApplicationClass(classFile: ClassFile): Boolean =
        project.get.allProjectClassFiles.toSet.contains(classFile)

}
