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
import boomerang.scope.test.targets.A
import boomerang.scope.test.targets.ParameterLocalsTarget
import boomerang.scope.test.targets.ThisLocalTarget
import java.util
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.opalj.br.IntegerType

class OpalLocalTest {

  private val integerType = IntegerType.toJVMTypeName

  @Test
  def thisLocalTest(): Unit = {
    val opalSetup = new OpalSetup()
    opalSetup.setupOpal(classOf[ThisLocalTarget].getName)

    val signature =
      new MethodSignature(classOf[ThisLocalTarget].getName, "call", "Void")
    val method = opalSetup.resolveMethod(signature)
    val opalMethod = OpalMethod.of(method, opalSetup.project.get)

    var checked = false
    opalMethod.getStatements.forEach(stmt => {
      if (stmt
            .containsInvokeExpr() && stmt.getInvokeExpr.getDeclaredMethod.getName
            .equals("callWithThis")
      ) {
        val invokeExpr = stmt.getInvokeExpr
        val base = invokeExpr.getBase

        Assertions.assertTrue(opalMethod.getThisLocal.equals(base))
        Assertions.assertTrue(base.equals(opalMethod.getThisLocal))
        Assertions.assertTrue(opalMethod.isThisLocal(base))

        checked = true
      }
    })

    Assertions.assertTrue(checked)
  }

  @Test
  def parameterLocalTest(): Unit = {
    val opalSetup = new OpalSetup()
    opalSetup.setupOpal(classOf[ParameterLocalsTarget].getName)

    // No parameters
    val noArgsSignature = new MethodSignature(
      classOf[ParameterLocalsTarget].getName,
      "noParameters",
      "Void"
    )
    val noArgs = opalSetup.resolveMethod(noArgsSignature)
    val noArgsMethod = OpalMethod.of(noArgs, opalSetup.project.get)

    Assertions.assertTrue(noArgsMethod.getParameterLocals.isEmpty)

    // One parameter (primitive type)
    val oneArgSignature = new MethodSignature(
      classOf[ParameterLocalsTarget].getName,
      "oneParameter",
      "Void",
      util.List.of(integerType)
    )
    val oneArg = opalSetup.resolveMethod(oneArgSignature)
    val oneArgMethod = OpalMethod.of(oneArg, opalSetup.project.get)

    Assertions.assertEquals(1, oneArgMethod.getParameterLocals.size)
    Assertions.assertEquals(
      "int",
      oneArgMethod.getParameterLocal(0).getType.toString
    )

    // Two parameters (primitive type + RefType)
    val twoArgSignature = new MethodSignature(
      classOf[ParameterLocalsTarget].getName,
      "twoParameters",
      "Void",
      util.List.of(integerType, s"L${classOf[A].getName}L")
    )
    val twoArgs = opalSetup.resolveMethod(twoArgSignature)
    val twoArgsMethod = OpalMethod.of(twoArgs, opalSetup.project.get)

    Assertions.assertEquals(2, twoArgsMethod.getParameterLocals.size)
    Assertions.assertEquals(
      "int",
      twoArgsMethod.getParameterLocal(0).getType.toString
    )
  }
}
