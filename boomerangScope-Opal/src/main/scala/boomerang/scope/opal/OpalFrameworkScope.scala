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

import boomerang.scope._
import org.opalj.br.analyses.Project

/**
 * Framework scope implementation for the static analysis framework Opal
 *
 * @param project the project to be analyzed
 * @param callGraph the generated Opal call graph
 * @param entryPoints the entry points
 * @param dataFlowScope the dataflow scope to be used in the analysis
 */
class OpalFrameworkScope(
    project: Project[_],
    callGraph: org.opalj.tac.cg.CallGraph,
    entryPoints: Set[org.opalj.br.Method],
    dataFlowScope: DataFlowScope
) extends FrameworkScope {

  private val opalCallGraph = new OpalCallGraph(project, callGraph, entryPoints)

  override def getCallGraph: CallGraph = opalCallGraph

  override def getDataFlowScope: DataFlowScope = dataFlowScope
}

object OpalFrameworkScope {
  final val STATIC_INITIALIZER: String = "<clinit>"
  final val CONSTRUCTOR: String = "<init>"
}
