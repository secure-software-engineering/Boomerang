package boomerang.scope.opal

import boomerang.scope.opal.tac.OpalMethod
import boomerang.scope.test.MethodSignature
import boomerang.scope.test.targets.AssignmentTarget
import org.junit.{Assert, Test}

class OpalAssignmentTest {

  @Test
  def arrayAllocationTest(): Unit = {
    val opalSetup = new OpalSetup()
    opalSetup.setupOpal(classOf[AssignmentTarget].getName)

    val signature = new MethodSignature(classOf[AssignmentTarget].getName, "arrayAllocation", "Void")
    val method = opalSetup.resolveMethod(signature)
    val opalMethod = OpalMethod(method)

    var arrayAllocationCount = 0
    opalMethod.getStatements.forEach(stmt => {
      if (stmt.isAssignStmt) {
        val rightOp = stmt.getRightOp

        if (rightOp.isArrayAllocationVal) {
          arrayAllocationCount += 1

          val leftOp = stmt.getLeftOp
          Assert.assertTrue(leftOp.isLocal)

          Assert.assertTrue(rightOp.getArrayAllocationSize.isLocal)
          Assert.assertTrue(rightOp.getType.isArrayType)
        }
      }
    })

    Assert.assertEquals(1, arrayAllocationCount)
  }

  @Test
  def constantAssignmentTest(): Unit = {
    val opalSetup = new OpalSetup()
    opalSetup.setupOpal(classOf[AssignmentTarget].getName)

    val signature = new MethodSignature(classOf[AssignmentTarget].getName, "constantAssignment", "Void")
    val method = opalSetup.resolveMethod(signature)
    val opalMethod = OpalMethod(method)

    var constantCount = 0
    opalMethod.getStatements.forEach(stmt => {
      if (stmt.isAssignStmt) {
        val leftOp = stmt.getLeftOp
        val rightOp = stmt.getRightOp

        Assert.assertTrue(leftOp.isLocal)

        if (rightOp.isIntConstant) {
          constantCount += 1

          Assert.assertEquals(10, rightOp.getIntValue)
          Assert.assertTrue(rightOp.getType.toString.equals("int"))
        }

        if (rightOp.isLongConstant) {
          constantCount += 1

          Assert.assertEquals(1000, rightOp.getLongValue)
          Assert.assertTrue(rightOp.getType.toString.equals("long"))
        }

        if (rightOp.isStringConstant) {
          constantCount += 1

          Assert.assertTrue(rightOp.getStringValue.equals("test"))
          Assert.assertTrue(rightOp.getType.toString.equals("java.lang.String"))
        }
      }
    })

    Assert.assertEquals(3, constantCount)
  }

  @Test
  def fieldStoreAssignmentTest(): Unit = {
    val opalSetup = new OpalSetup()
    opalSetup.setupOpal(classOf[AssignmentTarget].getName)

    val signature = new MethodSignature(classOf[AssignmentTarget].getName, "fieldStoreAssignment", "Void")
    val method = opalSetup.resolveMethod(signature)
    val opalMethod = OpalMethod(method)

    opalMethod.getStatements.forEach(stmt => {
      if (stmt.isFieldStore) {
        Assert.assertTrue(stmt.isAssignStmt)

        val leftOp = stmt.getLeftOp
        val rightOp = stmt.getRightOp

        println()
      }
    })
  }
}
