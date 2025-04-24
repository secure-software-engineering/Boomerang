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
import org.junit.Assert
import org.junit.Test
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
        val opalMethod = OpalMethod(method)

        var checked = false
        opalMethod.getStatements.forEach(stmt => {
            if (stmt
                    .containsInvokeExpr() && stmt.getInvokeExpr.getDeclaredMethod.getName
                    .equals("callWithThis")
            ) {
                val invokeExpr = stmt.getInvokeExpr
                val base = invokeExpr.getBase

                Assert.assertTrue(opalMethod.getThisLocal.equals(base))
                Assert.assertTrue(base.equals(opalMethod.getThisLocal))
                Assert.assertTrue(opalMethod.isThisLocal(base))

                checked = true
            }
        })

        Assert.assertTrue(checked)
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
        val noArgsMethod = OpalMethod(noArgs)

        Assert.assertTrue(noArgsMethod.getParameterLocals.isEmpty)

        // One parameter (primitive type)
        val oneArgSignature = new MethodSignature(
            classOf[ParameterLocalsTarget].getName,
            "oneParameter",
            "Void",
            util.List.of(integerType)
        )
        val oneArg = opalSetup.resolveMethod(oneArgSignature)
        val oneArgMethod = OpalMethod(oneArg)

        Assert.assertEquals(1, oneArgMethod.getParameterLocals.size)
        Assert.assertEquals(
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
        val twoArgsMethod = OpalMethod(twoArgs)

        Assert.assertEquals(2, twoArgsMethod.getParameterLocals.size)
        Assert.assertEquals(
            "int",
            twoArgsMethod.getParameterLocal(0).getType.toString
        )
        Assert.assertEquals(
            classOf[A].getName,
            twoArgsMethod.getParameterLocal(1).getType.toString
        )
    }
}
