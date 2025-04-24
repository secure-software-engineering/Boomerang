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
import boomerang.scope.test.targets.FieldClass
import boomerang.scope.test.targets.FieldTarget
import org.junit.Assert
import org.junit.Test

class OpalFieldTest {

  @Test
  def fieldLoadTest(): Unit = {
    val opalSetup = new OpalSetup()
    opalSetup.setupOpal(classOf[FieldTarget].getName)

    val signature =
      new MethodSignature(classOf[FieldTarget].getName, "fieldLoad", "Void")
    val method = opalSetup.resolveMethod(signature)
    val opalMethod = OpalMethod(method)

    var fieldLoadCount = 0
    opalMethod.getStatements.forEach(stmt => {
      if (stmt.isFieldLoad) {
        fieldLoadCount += 1

        val field = stmt.getLoadedField
        Assert.assertFalse(field.isPredefinedField)
        Assert.assertFalse(field.isInnerClassField)

        val fieldLoad = stmt.getFieldLoad
        val classType = fieldLoad.getX.getType.toString
        Assert.assertTrue(
          classType.equals("int") || classType.equals(
            classOf[FieldClass].getName
          )
        )

        val fieldType = fieldLoad.getY.getType.toString
        Assert.assertFalse(fieldLoad.getY.isPredefinedField)
        Assert.assertTrue(
          fieldType.equals("int") || fieldType.equals(classOf[A].getName)
        )
      }
    })

    Assert.assertEquals(2, fieldLoadCount)
  }

  @Test
  def fieldStoreTest(): Unit = {
    val opalSetup = new OpalSetup()
    opalSetup.setupOpal(classOf[FieldTarget].getName)

    val signature =
      new MethodSignature(classOf[FieldTarget].getName, "fieldStore", "Void")
    val method = opalSetup.resolveMethod(signature)
    val opalMethod = OpalMethod(method)

    var fieldStoreCount = 0
    opalMethod.getStatements.forEach(stmt => {
      if (stmt.isFieldStore) {
        fieldStoreCount += 1

        val fieldStore = stmt.getFieldStore
        val fieldClass = fieldStore.getX.getType.toString
        Assert.assertTrue(
          fieldClass.equals("int") || fieldClass.equals(
            classOf[FieldClass].getName
          )
        )

        val fieldType = fieldStore.getY.getType.toString
        Assert.assertFalse(fieldStore.getY.isPredefinedField)
        Assert.assertFalse(fieldStore.getY.isInnerClassField)
        Assert.assertTrue(
          fieldType.equals("int") || fieldType.equals(classOf[A].getName)
        )
      }
    })

    Assert.assertEquals(2, fieldStoreCount)
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
    val opalMethod = OpalMethod(method)

    var staticFieldLoadCount = 0
    opalMethod.getStatements.forEach(stmt => {
      if (stmt.isStaticFieldLoad) {
        staticFieldLoadCount += 1

        val staticField = stmt.getStaticField
        Assert.assertFalse(staticField.field().isPredefinedField)
        Assert.assertFalse(staticField.field().isInnerClassField)

        val typeName = staticField.getType.toString
        Assert.assertTrue(
          typeName.equals("int") || typeName.equals(classOf[A].getName)
        )
      }
    })

    Assert.assertEquals(2, staticFieldLoadCount)
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
    val opalMethod = OpalMethod(method)

    var staticFieldStoreCount = 0
    opalMethod.getStatements.forEach(stmt => {
      if (stmt.isStaticFieldStore) {
        staticFieldStoreCount += 1

        val staticField = stmt.getStaticField
        Assert.assertFalse(staticField.field().isPredefinedField)
        Assert.assertFalse(staticField.field().isInnerClassField)

        val typeName = staticField.getType.toString
        Assert.assertTrue(
          typeName.equals("int") || typeName.equals(classOf[A].getName)
        )
      }
    })

    Assert.assertEquals(2, staticFieldStoreCount)
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
    val opalMethod = OpalMethod(method)

    var fieldLoadCount = 0
    opalMethod.getStatements.forEach(stmt => {
      if (stmt.isFieldLoad) {
        fieldLoadCount += 1

        val field = stmt.getLoadedField
        Assert.assertFalse(field.isPredefinedField)
        Assert.assertTrue(field.isInnerClassField)

        val fieldLoad = stmt.getFieldLoad
        val classType = fieldLoad.getX.getType.toString
        Assert.assertTrue(
          classType.equals("int") || classType.equals(
            classOf[FieldClass.InnerFieldClass].getName
          )
        )

        val fieldType = fieldLoad.getY.getType.toString
        Assert.assertFalse(fieldLoad.getY.isPredefinedField)
        Assert.assertTrue(
          fieldType.equals("int") || fieldType.equals(classOf[A].getName)
        )
      }
    })

    Assert.assertEquals(2, fieldLoadCount)
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
    val opalMethod = OpalMethod(method)

    var fieldStoreCount = 0
    opalMethod.getStatements.forEach(stmt => {
      if (stmt.isFieldStore) {
        fieldStoreCount += 1

        val fieldStore = stmt.getFieldStore
        val classType = fieldStore.getX.getType.toString
        Assert.assertTrue(
          classType.equals("int") || classType.equals(
            classOf[FieldClass.InnerFieldClass].getName
          )
        )

        val fieldType = fieldStore.getY.getType.toString
        Assert.assertFalse(fieldStore.getY.isPredefinedField)
        Assert.assertTrue(fieldStore.getY.isInnerClassField)
        Assert.assertTrue(
          fieldType.equals("int") || fieldType.equals(classOf[A].getName)
        )
      }
    })

    Assert.assertEquals(2, fieldStoreCount)
  }
}
