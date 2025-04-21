package boomerang.scope.opal

import boomerang.scope.opal.tac.OpalMethod
import boomerang.scope.test.MethodSignature
import boomerang.scope.test.targets.ArrayTarget
import org.junit.{Assert, Ignore, Test}
import org.opalj.br.IntegerType

import java.util

class OpalArrayTest {

  private val integerType = IntegerType.toJVMTypeName

  @Test
  def arrayLoadIndexTest(): Unit = {
    val opalSetup = new OpalSetup()
    opalSetup.setupOpal(classOf[ArrayTarget].getName)

    val signature = new MethodSignature(classOf[ArrayTarget].getName, "arrayIndexLoad", "V", util.List.of("[" + integerType))
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
  def arrayLoadVarTest(): Unit = {
    val opalSetup = new OpalSetup()
    opalSetup.setupOpal(classOf[ArrayTarget].getName)

    val signature = new MethodSignature(classOf[ArrayTarget].getName, "arrayVarLoad", "V", util.List.of("[" + integerType))
    val method = opalSetup.resolveMethod(signature)
    val opalMethod = OpalMethod(method)

    var arrayLoadCount = 0
    opalMethod.getStatements.forEach(stmt => {
      if (stmt.isArrayLoad) {
        arrayLoadCount += 1

        val arrayBase = stmt.getArrayBase
        Assert.assertFalse(arrayBase.getX.isArrayRef)
        Assert.assertTrue(arrayBase.getX.isLocal)
        Assert.assertEquals(-1, arrayBase.getY)

        val rightOp = stmt.getRightOp
        Assert.assertTrue(rightOp.isArrayRef)
      }
    })

    Assert.assertEquals(1, arrayLoadCount)
  }

  @Test
  def arrayStoreIndexTest(): Unit = {
    val opalSetup = new OpalSetup()
    opalSetup.setupOpal(classOf[ArrayTarget].getName)

    val signature = new MethodSignature(classOf[ArrayTarget].getName, "arrayStoreIndex", "V")
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
  def arrayStoreVarTest(): Unit = {
    val opalSetup = new OpalSetup()
    opalSetup.setupOpal(classOf[ArrayTarget].getName)

    val signature = new MethodSignature(classOf[ArrayTarget].getName, "arrayStoreVar", "V")
    val method = opalSetup.resolveMethod(signature)
    val opalMethod = OpalMethod(method)

    var arrayStoreCount = 0
    opalMethod.getStatements.forEach(stmt => {
      if (stmt.isArrayStore) {
        arrayStoreCount += 1

        val arrayBase = stmt.getArrayBase
        Assert.assertFalse(arrayBase.getX.isArrayRef)
        Assert.assertTrue(arrayBase.getX.isLocal)
        Assert.assertEquals(-1, arrayBase.getY)

        val leftOp = stmt.getLeftOp
        Assert.assertTrue(leftOp.isArrayRef)
      }
    })

    Assert.assertEquals(1, arrayStoreCount)
  }
}
