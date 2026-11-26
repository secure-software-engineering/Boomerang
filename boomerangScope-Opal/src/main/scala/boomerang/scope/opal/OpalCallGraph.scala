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
import boomerang.scope.InvokeExpr
import boomerang.scope.LazyCallGraph
import boomerang.scope.Statement
import boomerang.scope.opal.tac.OpalFunctionInvokeExpr
import boomerang.scope.opal.tac.OpalMethod
import boomerang.scope.opal.tac.OpalMethodInvokeExpr
import boomerang.scope.opal.tac.OpalPhantomMethod
import boomerang.scope.opal.tac.OpalStatement
import java.util
import org.opalj.br.DeclaredMethod
import org.opalj.br.DefinedMethod
import org.opalj.br.Method
import org.opalj.br.MultipleDefinedMethods
import org.opalj.br.VirtualDeclaredMethod
import org.opalj.br.analyses.DeclaredMethods
import org.opalj.br.analyses.DeclaredMethodsKey
import org.opalj.br.analyses.Project
import org.opalj.tac.NonVirtualFunctionCall
import org.opalj.tac.StaticFunctionCall
import org.opalj.tac.VirtualFunctionCall
import scala.collection.mutable
import scala.jdk.CollectionConverters._

/**
 * Forwards to Opal's call graph. The Opal graph is kept alive by this object, together with the
 * property store it reads from.
 */
class OpalCallGraph(
    project: Project[_],
    callGraph: org.opalj.tac.cg.CallGraph,
    entryPoints: Set[Method]
) extends LazyCallGraph {

  private val declaredMethods: DeclaredMethods = project.get(DeclaredMethodsKey)

  // One OpalMethod (and thus one TAC body) per method, shared by all edges
  private val methods = mutable.HashMap.empty[Method, OpalMethod]

  // Call site statements of a method by the pc of their invoke expression
  private val callSitesByPc = mutable.HashMap.empty[OpalMethod, Map[Int, OpalStatement]]

  private def methodOf(method: Method): OpalMethod =
    methods.getOrElseUpdate(method, OpalMethod.of(method, project))

  private def callSitesOf(method: OpalMethod): Map[Int, OpalStatement] =
    callSitesByPc.getOrElseUpdate(
      method,
      method.tac.statements
        .map(stmt => new OpalStatement(stmt, method))
        .filter(_.containsInvokeExpr())
        .map(stmt => getPcForInvokeExpr(stmt.getInvokeExpr) -> stmt)
        .toMap
    )

  private def toTargets(callee: DeclaredMethod, callSite: Statement): Seq[boomerang.scope.Method] =
    callee match {
      case definedMethod: DefinedMethod =>
        val method = definedMethod.definedMethod

        if (method.body.isDefined) {
          Seq(methodOf(method))
        } else {
          Seq(
            OpalPhantomMethod.of(
              definedMethod.declaringClassType,
              definedMethod.name,
              definedMethod.descriptor,
              method.isStatic,
              project
            )
          )
        }
      case virtualMethod: VirtualDeclaredMethod =>
        Seq(
          OpalPhantomMethod.of(
            virtualMethod.declaringClassType,
            virtualMethod.name,
            virtualMethod.descriptor,
            callSite.getInvokeExpr.isStaticInvokeExpr,
            project
          )
        )
      case definedMethods: MultipleDefinedMethods =>
        val targets = Seq.newBuilder[boomerang.scope.Method]
        definedMethods.foreachDefinedMethod(method => targets += methodOf(method))
        targets.result()
    }

  override protected def computeEdgesOutOf(callSite: Statement): util.Collection[CallGraph.Edge] = {
    val edges = new util.ArrayList[CallGraph.Edge]()

    callSite.getMethod match {
      case caller: OpalMethod =>
        // Due to inlining variables, the PC's of statements and invoke expressions may differ
        val invokeExprPc = getPcForInvokeExpr(callSite.getInvokeExpr)
        val callees = callGraph.directCalleesOf(declaredMethods(caller.delegate), invokeExprPc)

        callees.foreach(callee =>
          toTargets(callee.method, callSite).foreach(target => edges.add(new CallGraph.Edge(callSite, target)))
        )
      case _ =>
    }

    edges
  }

  override protected def computeEdgesInto(callee: boomerang.scope.Method): util.Collection[CallGraph.Edge] =
    callee match {
      case method: OpalMethod =>
        val edges = new util.ArrayList[CallGraph.Edge]()

        callGraph
          .callersOf(declaredMethods(method.delegate))
          .iterator
          .foreach {
            case (caller: DefinedMethod, pc, true) if caller.definedMethod.body.isDefined =>
              callSitesOf(methodOf(caller.definedMethod))
                .get(pc)
                .foreach(callSite => edgesOutOf(callSite).forEach(edge => if (edge.tgt() == callee) edges.add(edge)))
            case _ =>
          }

        edges
      case _ =>
        // Phantom callees have no declared method to ask Opal about; fall back to all edges
        getEdges.asScala.filter(_.tgt() == callee).asJavaCollection
    }

  override protected def computeEntryPoints(): util.Collection[boomerang.scope.Method] =
    entryPoints.toSeq.filter(_.body.isDefined).map(methodOf(_): boomerang.scope.Method).asJavaCollection

  override protected def computeReachableMethods(): util.Collection[boomerang.scope.Method] = {
    val reachable = new util.LinkedHashSet[boomerang.scope.Method]()

    callGraph
      .reachableMethods()
      .foreach(context => {
        context.method match {
          case definedMethod: DefinedMethod if definedMethod.definedMethod.body.isDefined =>
            // Resolve targets without going through edgesOutOf to not memoize every edge
            val caller = methodOf(definedMethod.definedMethod)
            callSitesOf(caller).foreach { case (pc, callSite) =>
              callGraph
                .directCalleesOf(definedMethod, pc)
                .foreach(callee => toTargets(callee.method, callSite).foreach(reachable.add))
            }
          // TODO Should this case be considered?
          // case definedMethods: MultipleDefinedMethods =>
          case _ =>
        }
      })

    reachable
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
}
