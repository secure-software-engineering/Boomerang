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
import boomerang.scope.test.targets.ArrayTarget
import java.util
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.opalj.br.IntegerType

class OpalArrayTest {

  private val integerType = IntegerType.toJVMTypeName

  @Test
  def arrayLoadIndexTest(): Unit = {
    val opalSetup = new OpalSetup()
    opalSetup.setupOpal(classOf[ArrayTarget].getName)

    val signature = new MethodSignature(
      classOf[ArrayTarget].getName,
      "arrayIndexLoad",
      "V",
      util.List.of("[" + integerType)
    )
    val method = opalSetup.resolveMethod(signature)
    val opalMethod = OpalMethod.of(method, opalSetup.project.get)

    var arrayLoadCount = 0
    opalMethod.getStatements.forEach(stmt => {
      if (stmt.isArrayLoad) {
        arrayLoadCount += 1

        val arrayBase = stmt.getArrayBase
        Assertions.assertFalse(arrayBase.getBase.isArrayRef)
        Assertions.assertTrue(arrayBase.getBase.isLocal)
        Assertions.assertEquals(1, arrayBase.getIndex)

        val rightOp = stmt.getRightOp
        Assertions.assertTrue(rightOp.isArrayRef)
      }
    })

    Assertions.assertEquals(1, arrayLoadCount)
  }

  @Test
  def arrayLoadVarTest(): Unit = {
    val opalSetup = new OpalSetup()
    opalSetup.setupOpal(classOf[ArrayTarget].getName)

    val signature = new MethodSignature(
      classOf[ArrayTarget].getName,
      "arrayVarLoad",
      "V",
      util.List.of("[" + integerType)
    )
    val method = opalSetup.resolveMethod(signature)
    val opalMethod = OpalMethod.of(method, opalSetup.project.get)

    var arrayLoadCount = 0
    opalMethod.getStatements.forEach(stmt => {
      if (stmt.isArrayLoad) {
        arrayLoadCount += 1

        val arrayBase = stmt.getArrayBase
        Assertions.assertFalse(arrayBase.getBase.isArrayRef)
        Assertions.assertTrue(arrayBase.getBase.isLocal)
        Assertions.assertEquals(-1, arrayBase.getIndex)

        val rightOp = stmt.getRightOp
        Assertions.assertTrue(rightOp.isArrayRef)
      }
    })

    Assertions.assertEquals(1, arrayLoadCount)
  }

  @Test
  def arrayStoreIndexTest(): Unit = {
    val opalSetup = new OpalSetup()
    opalSetup.setupOpal(classOf[ArrayTarget].getName)

    val signature =
      new MethodSignature(classOf[ArrayTarget].getName, "arrayStoreIndex", "V")
    val method = opalSetup.resolveMethod(signature)
    val opalMethod = OpalMethod.of(method, opalSetup.project.get)

    var arrayStoreCount = 0
    opalMethod.getStatements.forEach(stmt => {
      if (stmt.isArrayStore) {
        arrayStoreCount += 1

        val arrayBase = stmt.getArrayBase
        Assertions.assertFalse(arrayBase.getBase.isArrayRef)
        Assertions.assertTrue(arrayBase.getBase.isLocal)
        Assertions.assertEquals(0, arrayBase.getIndex)

        val leftOp = stmt.getLeftOp
        Assertions.assertTrue(leftOp.isArrayRef)
      }
    })

    Assertions.assertEquals(1, arrayStoreCount)
  }

  @Test
  def arrayStoreVarTest(): Unit = {
    val opalSetup = new OpalSetup()
    opalSetup.setupOpal(classOf[ArrayTarget].getName)

    val signature =
      new MethodSignature(classOf[ArrayTarget].getName, "arrayStoreVar", "V")
    val method = opalSetup.resolveMethod(signature)
    val opalMethod = OpalMethod.of(method, opalSetup.project.get)

    var arrayStoreCount = 0
    opalMethod.getStatements.forEach(stmt => {
      if (stmt.isArrayStore) {
        arrayStoreCount += 1

        val arrayBase = stmt.getArrayBase
        Assertions.assertFalse(arrayBase.getBase.isArrayRef)
        Assertions.assertTrue(arrayBase.getBase.isLocal)
        Assertions.assertEquals(-1, arrayBase.getIndex)

        val leftOp = stmt.getLeftOp
        Assertions.assertTrue(leftOp.isArrayRef)
      }
    })

    Assertions.assertEquals(1, arrayStoreCount)
  }
}
