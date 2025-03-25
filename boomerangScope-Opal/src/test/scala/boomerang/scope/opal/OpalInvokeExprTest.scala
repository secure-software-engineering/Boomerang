package boomerang.scope.opal

import boomerang.scope.DataFlowScope
import boomerang.scope.opal.tac.OpalMethod
import boomerang.scope.test.MethodSignature
import boomerang.scope.test.targets.{InvokeExprTarget, SingleTarget}
import org.junit.Test
import org.opalj.tac.cg.{CHACallGraphKey, CallGraph}

import java.util
import java.util.Set
import scala.jdk.javaapi.CollectionConverters

class OpalInvokeExprTest {

  @Test
  def instanceInvokeExprTest(): Unit = {
    val opalSetup = new OpalSetup()
    opalSetup.setupOpal(classOf[SingleTarget].getName)

    val signature = new MethodSignature(classOf[SingleTarget].getName, "identityTest", "Void")
    val method = opalSetup.resolveMethod(signature)
    val opalMethod = OpalMethod(method)
    opalMethod.getControlFlowGraph


    val callGraph: CallGraph = OpalClient.project.get.get(CHACallGraphKey)

    val scope = new OpalFrameworkScope(OpalClient.project.get, callGraph, CollectionConverters.asScala(util.Set.of(method)).toSet, DataFlowScope.EXCLUDE_PHANTOM_CLASSES)

    println(opalMethod.tac.statements.mkString("Array(\n\t", "\n\t", "\n)"))
  }

}
