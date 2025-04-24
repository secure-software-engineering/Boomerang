/**
 * ***************************************************************************** 
 * Copyright (c) 2025 Fraunhofer IEM, Paderborn, Germany. This program and the
 * accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at http://www.eclipse.org/legal/epl-2.0.
 *
 * <p>SPDX-License-Identifier: EPL-2.0
 *
 * <p>Contributors: Johannes Spaeth - initial API and implementation
 * *****************************************************************************
 */
package boomerang.scope.opal

import boomerang.scope._
import java.util.stream
import org.opalj.br.analyses.Project

class OpalFrameworkScope(
    project: Project[_],
    callGraph: org.opalj.tac.cg.CallGraph,
    entryPoints: Set[org.opalj.br.Method],
    dataFlowScope: DataFlowScope
) extends FrameworkScope {

    OpalClient.init(project)
    private val opalCallGraph = new OpalCallGraph(project, callGraph, entryPoints)

    override def getCallGraph: CallGraph = opalCallGraph

    override def getDataFlowScope: DataFlowScope = dataFlowScope

    override def getTrueValue(m: Method): Val = ???

    override def getFalseValue(m: Method): Val = ???

    override def handleStaticFieldInitializers(fact: Val): stream.Stream[Method] =
        ???

    override def newStaticFieldVal(field: Field, m: Method): StaticFieldVal = ???
}

object OpalFrameworkScope {
    final val STATIC_INITIALIZER: String = "<clinit>"
    final val CONSTRUCTOR: String = "<init>"
}
