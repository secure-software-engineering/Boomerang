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

import boomerang.scope.test.MethodSignature
import boomerang.scope.test.targets.ScopeTarget
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.opalj.br.IntegerType
import scala.jdk.CollectionConverters._

class OpalScopeTest {

  @Test
  def testFactoryAndConverter(): Unit = {
    val opalSetup = new OpalSetup
    opalSetup.setupOpal(classOf[ScopeTarget].getName)

    val signature = new MethodSignature(classOf[ScopeTarget].getName, "methodWithStatements", IntegerType.toJVMTypeName)
    val method = opalSetup.resolveMethod(signature)
    val project = opalSetup.project.get

    // Test method factory and converter
    val opalMethod = OpalScopeFactory.createOpalMethod(method, project)
    val newOpalMethod = OpalScopeConverter.toOpalMethod(opalMethod)
    Assertions.assertEquals(method, newOpalMethod)

    // Test phantom method factory and converter
    val phantomMethod = OpalScopeFactory.createOpalPhantomMethod(
      method.classFile.thisType,
      method.name,
      method.descriptor,
      method.isStatic,
      project
    )
    val newPhantomMethod = OpalScopeConverter.toOpalPhantomMethod(phantomMethod)
    Assertions.assertEquals(phantomMethod, newPhantomMethod)

    // Test class factory and converter
    val classType = method.classFile.thisType
    val wrappedClass = OpalScopeFactory.createOpalWrappedClass(classType, project)
    val newClassType = OpalScopeConverter.toOpalClass(wrappedClass)
    Assertions.assertEquals(classType, newClassType)

    var checkedField = false
    var checkedType = false
    var checkedVal = false

    opalMethod.getStatements.asScala.foreach(statement => {
      // Test statement factory and converter
      val opalStmt = OpalScopeConverter.toOpalStatement(statement)
      val newStmt = OpalScopeFactory.createOpalStatement(opalStmt, opalMethod)
      Assertions.assertEquals(statement, newStmt)

      // Test statement factory and converter unsafe
      val opalStmtUnsafe = OpalScopeConverter.toOpalStatement(statement)
      val newStmtUnsafe = OpalScopeFactory.createOpalStatementUnsafe(opalStmtUnsafe, opalMethod)
      Assertions.assertEquals(statement, newStmtUnsafe)

      // Test field factory and converter
      if (statement.isFieldLoad) {
        val field = statement.getLoadedField
        val fieldRef = OpalScopeConverter.toOpalField(field)
        val newField = OpalScopeFactory.createOpalField(fieldRef.declaringClass, fieldRef.fieldType, fieldRef.name)
        Assertions.assertEquals(field, newField)

        checkedField = true
      }

      // Test type factory and converter
      if (statement.isAssignStmt && statement.getRightOp.isNewExpr) {
        val t = statement.getRightOp.getNewExprType
        val opalType = OpalScopeConverter.toOpalType(t)
        val newType = OpalScopeFactory.createOpalType(opalType, project)
        Assertions.assertEquals(t, newType)

        checkedType = true
      }

      // Test val factory and converter
      if (statement.isAssignStmt && statement.getRightOp.isIntConstant) {
        val rightOp = statement.getRightOp
        val value = OpalScopeConverter.toOpalExpr(rightOp)
        val newRightOp = OpalScopeFactory.createOpalVal(value, opalMethod)
        Assertions.assertEquals(rightOp, newRightOp)

        val valueUnsafe = OpalScopeConverter.toOpalExpr(rightOp)
        val newRightOpUnsafe = OpalScopeFactory.createOpalValUnsafe(valueUnsafe, opalMethod)
        Assertions.assertEquals(rightOp, newRightOpUnsafe)

        checkedVal = true
      }
    })

    Assertions.assertTrue(checkedField)
    Assertions.assertTrue(checkedType)
    Assertions.assertTrue(checkedVal)
  }
}
