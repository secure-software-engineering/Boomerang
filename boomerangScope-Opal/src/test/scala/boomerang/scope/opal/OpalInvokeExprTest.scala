package boomerang.scope.opal

import boomerang.scope.opal.tac.OpalMethod
import boomerang.scope.opal.transformer.TacTransformer
import boomerang.scope.test.MethodSignature
import boomerang.scope.test.targets.InvokeExprTarget
import org.junit.Test

class OpalInvokeExprTest {

  @Test
  def instanceInvokeExprTest(): Unit = {
    val opalSetup = new OpalSetup()
    opalSetup.setupOpal(classOf[InvokeExprTarget].getName)

    val signature = new MethodSignature(classOf[InvokeExprTarget].getName, "alias", "Void")
    val method = opalSetup.resolveMethod(signature)
    val opalMethod = OpalMethod(method)

    val transformedTac = TacTransformer(opalMethod.tacCode)
    println(transformedTac.mkString("Array(", "\n", ")"))
  }

}
