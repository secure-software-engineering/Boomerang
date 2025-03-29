package boomerang.scope.opal

import boomerang.scope.opal.tac.OpalMethod
import boomerang.scope.test.MethodSignature
import boomerang.scope.test.targets.ArrayTarget
import org.junit.{Assert, Test}
import org.opalj.br.IntegerType

import java.util

class OpalArrayTest {

  private val integerType = IntegerType.toJVMTypeName

  @Test
  def singleArrayLoadTest(): Unit = {
    val opalSetup = new OpalSetup()
    opalSetup.setupOpal(classOf[ArrayTarget].getName)

    val signature = new MethodSignature(classOf[ArrayTarget].getName, "singleArrayLoad", "Void", util.List.of("[" + integerType))
    val method = opalSetup.resolveMethod(signature)
    val opalMethod = OpalMethod(method)

    var arrayLoadCount = 0
    opalMethod.getStatements.forEach(stmt => {
      if (stmt.isArrayLoad) {
        arrayLoadCount += 1

        val arrayBase = stmt.getArrayBase
        Assert.assertFalse(arrayBase.getX.isArrayRef)
        Assert.assertTrue(arrayBase.getX.isLocal)
        Assert.assertEquals(1, arrayBase.getY)

        val rightOp = stmt.getRightOp
        Assert.assertTrue(rightOp.isArrayRef)
      }
    })

    Assert.assertEquals(1, arrayLoadCount)
  }

  @Test
  def singleArrayStoreTest(): Unit = {
    val opalSetup = new OpalSetup()
    opalSetup.setupOpal(classOf[ArrayTarget].getName)

    val signature = new MethodSignature(classOf[ArrayTarget].getName, "singleArrayStore", "Void")
    val method = opalSetup.resolveMethod(signature)
    val opalMethod = OpalMethod(method)

    var arrayStoreCount = 0
    opalMethod.getStatements.forEach(stmt => {
      if (stmt.isArrayStore) {
        arrayStoreCount += 1

        val arrayBase = stmt.getArrayBase
        Assert.assertFalse(arrayBase.getX.isArrayRef)
        Assert.assertTrue(arrayBase.getX.isLocal)
        Assert.assertEquals(0, arrayBase.getY)

        val leftOp = stmt.getLeftOp
        Assert.assertTrue(leftOp.isArrayRef)
      }
    })

    Assert.assertEquals(1, arrayStoreCount)
  }

  @Test
  def multiArrayStore(): Unit = {
    val opalSetup = new OpalSetup()
    opalSetup.setupOpal(classOf[ArrayTarget].getName)

    val signature = new MethodSignature(classOf[ArrayTarget].getName, "multiArrayStore", "Void")
    val method = opalSetup.resolveMethod(signature)
    val opalMethod = OpalMethod(method)

    println(opalMethod.tac.statements.mkString("Array(\n\t", "\n\t", "\n)"))
  }

}
