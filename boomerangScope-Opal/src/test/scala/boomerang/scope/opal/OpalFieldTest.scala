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
import boomerang.scope.test.targets.FieldTarget
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class OpalFieldTest {

  @Test
  def fieldLoadTest(): Unit = {
    val opalSetup = new OpalSetup()
    opalSetup.setupOpal(classOf[FieldTarget].getName)

    val signature =
      new MethodSignature(classOf[FieldTarget].getName, "fieldLoad", "Void")
    val method = opalSetup.resolveMethod(signature)
    val opalMethod = OpalMethod.of(method, opalSetup.project.get)

    var fieldLoadCount = 0
    opalMethod.getStatements.forEach(stmt => {
      if (stmt.isFieldLoad) {
        fieldLoadCount += 1

        val field = stmt.getLoadedField
        Assertions.assertFalse(field.isPredefinedField)
        Assertions.assertFalse(field.isInnerClassField)
      }
    })

    Assertions.assertEquals(2, fieldLoadCount)
  }

  @Test
  def fieldStoreTest(): Unit = {
    val opalSetup = new OpalSetup()
    opalSetup.setupOpal(classOf[FieldTarget].getName)

    val signature =
      new MethodSignature(classOf[FieldTarget].getName, "fieldStore", "Void")
    val method = opalSetup.resolveMethod(signature)
    val opalMethod = OpalMethod.of(method, opalSetup.project.get)

    var fieldStoreCount = 0
    opalMethod.getStatements.forEach(stmt => {
      if (stmt.isFieldStore) {
        fieldStoreCount += 1

        val fieldStore = stmt.getFieldStore
        Assertions.assertFalse(fieldStore.getField.isPredefinedField)
        Assertions.assertFalse(fieldStore.getField.isInnerClassField)
      }
    })

    Assertions.assertEquals(2, fieldStoreCount)
  }

  @Test
  def staticFieldLoadTest(): Unit = {
    val opalSetup = new OpalSetup()
    opalSetup.setupOpal(classOf[FieldTarget].getName)

    val signature = new MethodSignature(
      classOf[FieldTarget].getName,
      "staticFieldLoad",
      "Void"
    )
    val method = opalSetup.resolveMethod(signature)
    val opalMethod = OpalMethod.of(method, opalSetup.project.get)

    var staticFieldLoadCount = 0
    opalMethod.getStatements.forEach(stmt => {
      if (stmt.isStaticFieldLoad) {
        staticFieldLoadCount += 1

        val staticField = stmt.getStaticField.asStaticFieldVal()
        Assertions.assertFalse(staticField.getField.isPredefinedField)
        Assertions.assertFalse(staticField.getField.isInnerClassField)

        val typeName = staticField.getType.toString
        Assertions.assertTrue(
          typeName.equals("int") || typeName.equals(classOf[A].getName)
        )
      }
    })

    Assertions.assertEquals(2, staticFieldLoadCount)
  }

  @Test
  def staticFieldStoreTest(): Unit = {
    val opalSetup = new OpalSetup()
    opalSetup.setupOpal(classOf[FieldTarget].getName)

    val signature = new MethodSignature(
      classOf[FieldTarget].getName,
      "staticFieldStore",
      "Void"
    )
    val method = opalSetup.resolveMethod(signature)
    val opalMethod = OpalMethod.of(method, opalSetup.project.get)

    var staticFieldStoreCount = 0
    opalMethod.getStatements.forEach(stmt => {
      if (stmt.isStaticFieldStore) {
        staticFieldStoreCount += 1

        val staticField = stmt.getStaticField
        Assertions.assertFalse(staticField.getField.isPredefinedField)
        Assertions.assertFalse(staticField.getField.isInnerClassField)

        val typeName = staticField.asStaticFieldVal().getType.toString
        Assertions.assertTrue(
          typeName.equals("int") || typeName.equals(classOf[A].getName)
        )
      }
    })

    Assertions.assertEquals(2, staticFieldStoreCount)
  }

  @Test
  def innerFieldLoadTest(): Unit = {
    val opalSetup = new OpalSetup()
    opalSetup.setupOpal(classOf[FieldTarget].getName)

    val signature = new MethodSignature(
      classOf[FieldTarget].getName,
      "innerFieldLoad",
      "Void"
    )
    val method = opalSetup.resolveMethod(signature)
    val opalMethod = OpalMethod.of(method, opalSetup.project.get)

    var fieldLoadCount = 0
    opalMethod.getStatements.forEach(stmt => {
      if (stmt.isFieldLoad) {
        fieldLoadCount += 1

        val field = stmt.getLoadedField
        Assertions.assertFalse(field.isPredefinedField)
        Assertions.assertTrue(field.isInnerClassField)

        val fieldLoad = stmt.getFieldLoad
        Assertions.assertFalse(fieldLoad.getField.isPredefinedField)
      }
    })

    Assertions.assertEquals(2, fieldLoadCount)
  }

  @Test
  def innerFieldStoreTest(): Unit = {
    val opalSetup = new OpalSetup()
    opalSetup.setupOpal(classOf[FieldTarget].getName)

    val signature = new MethodSignature(
      classOf[FieldTarget].getName,
      "innerFieldStore",
      "Void"
    )
    val method = opalSetup.resolveMethod(signature)
    val opalMethod = OpalMethod.of(method, opalSetup.project.get)

    var fieldStoreCount = 0
    opalMethod.getStatements.forEach(stmt => {
      if (stmt.isFieldStore) {
        fieldStoreCount += 1

        val fieldStore = stmt.getFieldStore
        Assertions.assertFalse(fieldStore.getField.isPredefinedField)
      }
    })

    Assertions.assertEquals(2, fieldStoreCount)
  }
}
