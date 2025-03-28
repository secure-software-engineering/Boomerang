package boomerang.scope.opal

import boomerang.scope.{CallGraph, InvokeExpr}
import boomerang.scope.CallGraph.Edge
import boomerang.scope.opal.tac.{OpalFunctionInvokeExpr, OpalMethod, OpalMethodInvokeExpr, OpalPhantomMethod, OpalStatement}
import boomerang.scope.opal.transformer.TacTransformer
import org.opalj.br.{DefinedMethod, Method, MultipleDefinedMethods, VirtualDeclaredMethod}
import org.opalj.tac.{NonVirtualFunctionCall, StaticFunctionCall, VirtualFunctionCall}

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
        // Due to inlining variables, the PC's of statements and invoke expressions may differ
        val invokeExprPc = getPcForInvokeExpr(srcStatement.getInvokeExpr)
        val callees = callGraph.directCalleesOf(method, invokeExprPc)

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

  private def getPcForInvokeExpr(invokeExpr: InvokeExpr): Int = {
    invokeExpr match {
      case methodInvokeExpr: OpalMethodInvokeExpr => methodInvokeExpr.delegate.pc
      case functionInvokeExpr: OpalFunctionInvokeExpr =>
        functionInvokeExpr.delegate match {
          case call: NonVirtualFunctionCall[_] => call.pc
          case call: VirtualFunctionCall[_] => call.pc
          case call: StaticFunctionCall[_] => call.pc
          case _ => throw new RuntimeException("Unknown function call: " + functionInvokeExpr)
      }
      case _ => throw new RuntimeException("Unknown invoke expression: " + invokeExpr)
    }
  }

  entryPoints.foreach(entryPoint => {
    if (entryPoint.body.isDefined) {
      addEntryPoint(OpalMethod(entryPoint))
    }
  })
}
