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

import boomerang.scope.CallGraph
import boomerang.scope.CallGraph.Edge
import boomerang.scope.InvokeExpr
import boomerang.scope.opal.tac.OpalFunctionInvokeExpr
import boomerang.scope.opal.tac.OpalMethod
import boomerang.scope.opal.tac.OpalMethodInvokeExpr
import boomerang.scope.opal.tac.OpalPhantomMethod
import boomerang.scope.opal.tac.OpalStatement
import boomerang.scope.opal.transformation.TacBodyBuilder
import org.opalj.br.DefinedMethod
import org.opalj.br.Method
import org.opalj.br.MultipleDefinedMethods
import org.opalj.br.VirtualDeclaredMethod
import org.opalj.br.analyses.Project
import org.opalj.tac.NonVirtualFunctionCall
import org.opalj.tac.StaticFunctionCall
import org.opalj.tac.VirtualFunctionCall

class OpalCallGraph(
    project: Project[_],
    callGraph: org.opalj.tac.cg.CallGraph,
    entryPoints: Set[Method]
) extends CallGraph {

  callGraph
    .reachableMethods()
    .foreach(method => {
      method.method match {
        case definedMethod: DefinedMethod =>
          if (definedMethod.definedMethod.body.isDefined) {
            addEdgesFromMethod(definedMethod)
          }
        // TODO Should this case be considered?
        // case definedMethods: MultipleDefinedMethods =>
        //   definedMethods.foreachDefinedMethod(m => addEdgesFromMethod(m))
        case _ =>
      }
    })

  private def addEdgesFromMethod(method: DefinedMethod): Unit = {
    val tacCode = TacBodyBuilder(project, method.definedMethod)

    tacCode.statements.foreach(stmt => {
      val srcStatement =
        new OpalStatement(stmt, OpalMethod(method.definedMethod, tacCode))

      if (srcStatement.containsInvokeExpr()) {
        // Due to inlining variables, the PC's of statements and invoke expressions may differ
        val invokeExprPc = getPcForInvokeExpr(srcStatement.getInvokeExpr)
        val callees = callGraph.directCalleesOf(method, invokeExprPc)

        callees.foreach(callee => {
          callee.method match {
            case definedMethod: DefinedMethod =>
              val method = definedMethod.definedMethod

              if (method.body.isDefined) {
                val targetMethod = OpalMethod(method)

                addEdge(new Edge(srcStatement, targetMethod))
              } else {
                val targetMethod = OpalPhantomMethod(
                  definedMethod.declaringClassType,
                  definedMethod.name,
                  definedMethod.descriptor,
                  method.isStatic
                )

                addEdge(new Edge(srcStatement, targetMethod))
              }
            case virtualMethod: VirtualDeclaredMethod =>
              val targetMethod = OpalPhantomMethod(
                virtualMethod.declaringClassType,
                virtualMethod.name,
                virtualMethod.descriptor,
                srcStatement.getInvokeExpr.isStaticInvokeExpr
              )

              addEdge(new Edge(srcStatement, targetMethod))
            case definedMethods: MultipleDefinedMethods =>
              definedMethods.foreachDefinedMethod(method => {
                val targetMethod = OpalMethod(method)

                addEdge(new Edge(srcStatement, targetMethod))
              })
          }
        })
      }
    })
  }

  private def getPcForInvokeExpr(invokeExpr: InvokeExpr): Int = {
    invokeExpr match {
      case methodInvokeExpr: OpalMethodInvokeExpr =>
        methodInvokeExpr.delegate.pc
      case functionInvokeExpr: OpalFunctionInvokeExpr =>
        functionInvokeExpr.delegate match {
          case call: NonVirtualFunctionCall[_] => call.pc
          case call: VirtualFunctionCall[_] => call.pc
          case call: StaticFunctionCall[_] => call.pc
          case _ =>
            throw new RuntimeException(
              "Unknown function call: " + functionInvokeExpr
            )
        }
      case _ =>
        throw new RuntimeException("Unknown invoke expression: " + invokeExpr)
    }
  }

  // Explicitly add static initializers (<clinit>) as they are called only implicitly
  callGraph
    .reachableMethods()
    .foreach(method => {
      method.method match {
        case definedMethod: DefinedMethod if definedMethod.definedMethod.isStaticInitializer =>
          if (definedMethod.definedMethod.body.isDefined) {
            addEntryPoint(OpalMethod(definedMethod.definedMethod))
          }
        case _ =>
      }
    })

  entryPoints.foreach(entryPoint => {
    if (entryPoint.body.isDefined) {
      addEntryPoint(OpalMethod(entryPoint))
    }
  })
}
