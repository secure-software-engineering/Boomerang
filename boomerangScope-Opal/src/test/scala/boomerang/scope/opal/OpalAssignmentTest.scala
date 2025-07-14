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

import boomerang.scope.opal.tac.OpalMethod
import boomerang.scope.test.MethodSignature
import boomerang.scope.test.targets.AssignmentTarget
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class OpalAssignmentTest {

  @Test
  def arrayAllocationTest(): Unit = {
    val opalSetup = new OpalSetup()
    opalSetup.setupOpal(classOf[AssignmentTarget].getName)

    val signature = new MethodSignature(
      classOf[AssignmentTarget].getName,
      "arrayAllocation",
      "V"
    )
    val method = opalSetup.resolveMethod(signature)
    val opalMethod = OpalMethod.of(method, opalSetup.project.get)

    var arrayAllocationCount = 0
    opalMethod.getStatements.forEach(stmt => {
      if (stmt.isAssignStmt) {
        val rightOp = stmt.getRightOp

        if (rightOp.isArrayAllocationVal) {
          arrayAllocationCount += 1

          val leftOp = stmt.getLeftOp
          Assertions.assertTrue(leftOp.isLocal)

          Assertions.assertTrue(rightOp.getArrayAllocationSize.isIntConstant)
          Assertions.assertEquals(2, rightOp.getArrayAllocationSize.getIntValue)
        }
      }
    })

    Assertions.assertEquals(1, arrayAllocationCount)
  }

  @Test
  def multiArrayAllocationTest(): Unit = {
    val opalSetup = new OpalSetup()
    opalSetup.setupOpal(classOf[AssignmentTarget].getName)

    val signature = new MethodSignature(
      classOf[AssignmentTarget].getName,
      "multiArrayAllocation",
      "V"
    )
    val method = opalSetup.resolveMethod(signature)
    val opalMethod = OpalMethod.of(method, opalSetup.project.get)

    var checked = false
    opalMethod.getStatements.forEach(stmt => {
      if (stmt.isAssignStmt) {
        val rightOp = stmt.getRightOp

        if (rightOp.isArrayAllocationVal) {
          val leftOp = stmt.getLeftOp
          Assertions.assertTrue(leftOp.isLocal)

          val arraySize = rightOp.getArrayAllocationSize
          Assertions.assertTrue(arraySize.isIntConstant)
          Assertions.assertEquals(2, arraySize.getIntValue)

          checked = true
        }
      }
    })

    Assertions.assertTrue(checked)
  }

  @Test
  def constantAssignmentTest(): Unit = {
    val opalSetup = new OpalSetup()
    opalSetup.setupOpal(classOf[AssignmentTarget].getName)

    val signature = new MethodSignature(
      classOf[AssignmentTarget].getName,
      "constantAssignment",
      "Void"
    )
    val method = opalSetup.resolveMethod(signature)
    val opalMethod = OpalMethod.of(method, opalSetup.project.get)

    var constantCount = 0
    opalMethod.getStatements.forEach(stmt => {
      if (stmt.isAssignStmt) {
        val leftOp = stmt.getLeftOp
        val rightOp = stmt.getRightOp

        Assertions.assertTrue(leftOp.isLocal)

        if (rightOp.isIntConstant) {
          constantCount += 1

          Assertions.assertEquals(10, rightOp.getIntValue)
          Assertions.assertTrue(rightOp.getType.toString.equals("int"))
        }

        if (rightOp.isLongConstant) {
          constantCount += 1

          Assertions.assertEquals(1000, rightOp.getLongValue)
          Assertions.assertTrue(rightOp.getType.toString.equals("long"))
        }

        if (rightOp.isStringConstant) {
          constantCount += 1

          Assertions.assertTrue(rightOp.getStringValue.equals("test"))
          Assertions.assertTrue(rightOp.getType.toString.equals("java.lang.String"))
        }
      }
    })

    Assertions.assertEquals(3, constantCount)
  }

  @Test
  def fieldStoreAssignmentTest(): Unit = {
    val opalSetup = new OpalSetup()
    opalSetup.setupOpal(classOf[AssignmentTarget].getName)

    val signature = new MethodSignature(
      classOf[AssignmentTarget].getName,
      "fieldStoreAssignment",
      "Void"
    )
    val method = opalSetup.resolveMethod(signature)
    val opalMethod = OpalMethod.of(method, opalSetup.project.get)

    opalMethod.getStatements.forEach(stmt => {
      if (stmt.isFieldStore) {
        Assertions.assertTrue(stmt.isAssignStmt)
      }
    })
  }
}
