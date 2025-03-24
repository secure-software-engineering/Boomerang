package boomerang.scope.opal

import boomerang.scope.CallGraph
import boomerang.scope.CallGraph.Edge
import boomerang.scope.opal.tac.{OpalMethod, OpalPhantomMethod, OpalStatement}
import boomerang.scope.opal.transformer.{BoomerangTACode, TacTransformer}
import org.opalj.br.{DefinedMethod, Method, MultipleDefinedMethods, VirtualDeclaredMethod}

class OpalCallGraph(callGraph: org.opalj.tac.cg.CallGraph, entryPoints: Set[Method]) extends CallGraph {

  callGraph.reachableMethods().foreach(method => {
    method.method match {
      case definedMethod: DefinedMethod => addEdgesFromMethod(definedMethod)
      // TODO Should this case be considered?
      // case definedMethods: MultipleDefinedMethods =>
      //   definedMethods.foreachDefinedMethod(m => addEdgesFromMethod(m))
      case _ =>
    }
  })

  private def addEdgesFromMethod(method: DefinedMethod): Unit = {
    val tacCode = TacTransformer(method.definedMethod, OpalClient.getClassHierarchy)

    tacCode.statements.foreach(stmt => {
      val srcStatement = new OpalStatement(stmt, OpalMethod(method.definedMethod, tacCode))

      if (srcStatement.containsInvokeExpr()) {
        val callees = callGraph.directCalleesOf(method, stmt.pc)

        callees.foreach(callee => {
          callee.method match {
            case definedMethod: DefinedMethod =>
              val targetMethod = OpalMethod(definedMethod.definedMethod)

              addEdge(new Edge(srcStatement, targetMethod))
            case virtualMethod: VirtualDeclaredMethod =>
              val targetMethod = OpalPhantomMethod(virtualMethod, srcStatement.getInvokeExpr.isStaticInvokeExpr)

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

  entryPoints.foreach(entryPoint => {
    if (entryPoint.body.isDefined) {
      addEntryPoint(OpalMethod(entryPoint))
    }
  })
}
