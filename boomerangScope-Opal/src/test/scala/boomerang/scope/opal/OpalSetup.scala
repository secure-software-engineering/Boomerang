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

import boomerang.scope.test.MethodSignature
import boomerang.scope.test.TargetClassPath
import java.io.File
import org.opalj.br._
import org.opalj.br.analyses.Project
import org.opalj.log.DevNullLogger
import org.opalj.log.GlobalLogContext
import org.opalj.log.OPALLogger
import scala.collection.immutable.ArraySeq

class OpalSetup {

  var targetClass: Option[ClassFile] = None

  def setupOpal(targetClassName: String): Unit = {
    OPALLogger.updateLogger(GlobalLogContext, DevNullLogger)
    val project = Project(new File(TargetClassPath.TARGET_CLASS_PATH))

    OpalClient.init(project)
    targetClass = project.classFile(ObjectType(targetClassName.replace(".", "/")))
  }

  def resolveMethod(methodSignature: MethodSignature): Method = {
    val parameterFields =
      ArraySeq.from(methodSignature.getParameters.toArray.collect({
        case p: String => FieldType(p.replace(".", "/"))
        case _ => throw new IllegalArgumentException("No String")
      }))
    val returnType = ReturnType(methodSignature.getReturnType)

    val method = targetClass.get.findMethod(
      methodSignature.getMethodName,
      MethodDescriptor(parameterFields, returnType)
    )
    if (method.isEmpty) {
      throw new RuntimeException(
        "Could not find method " + methodSignature.getMethodName + " in class " + methodSignature.getDeclaringClass
      )
    }

    method.get
  }
}
