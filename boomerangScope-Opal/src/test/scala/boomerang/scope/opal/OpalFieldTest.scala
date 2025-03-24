package boomerang.scope.opal

import boomerang.scope.opal.tac.OpalMethod
import boomerang.scope.test.MethodSignature
import boomerang.scope.test.targets.{A, FieldClass, FieldTarget}
import org.junit.{Assert, Test}

class OpalFieldTest {

  @Test
  def fieldLoadTest(): Unit = {
    val opalSetup = new OpalSetup()
    opalSetup.setupOpal(classOf[FieldTarget].getName)

    val signature = new MethodSignature(classOf[FieldTarget].getName, "fieldLoad", "Void")
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
        //Assert.assertTrue(fieldLoad.getX.getType.toString.equals(classOf[FieldClass].getName))

        val fieldType = fieldLoad.getY.getType.toString
        Assert.assertFalse(fieldLoad.getY.isPredefinedField)
        //Assert.assertTrue(fieldType.equals("int") || fieldType.equals(classOf[A].getName))
      }
    })

    Assert.assertEquals(2, fieldLoadCount)
  }

  @Test
  def fieldStoreTest(): Unit = {
    val opalSetup = new OpalSetup()
    opalSetup.setupOpal(classOf[FieldTarget].getName)

    val signature = new MethodSignature(classOf[FieldTarget].getName, "fieldStore", "Void")
    val method = opalSetup.resolveMethod(signature)
    val opalMethod = OpalMethod(method)

    var fieldStoreCount = 0
    opalMethod.getStatements.forEach(stmt => {
      if (stmt.isFieldStore) {
        fieldStoreCount += 1

        val fieldStore = stmt.getFieldStore
        Assert.assertTrue(fieldStore.getX.getType.toString.equals(classOf[FieldClass].getName))

        val fieldType = fieldStore.getY.getType.toString
        Assert.assertFalse(fieldStore.getY.isPredefinedField)
        Assert.assertFalse(fieldStore.getY.isInnerClassField)
        //Assert.assertTrue(fieldType.equals("int") || fieldType.equals(classOf[A].getName))
      }
    })

    Assert.assertEquals(2, fieldStoreCount)
  }

  @Test
  def staticFieldLoadTest(): Unit = {
    val opalSetup = new OpalSetup()
    opalSetup.setupOpal(classOf[FieldTarget].getName)

    val signature = new MethodSignature(classOf[FieldTarget].getName, "staticFieldLoad", "Void")
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
        //Assert.assertTrue(typeName.equals("int") || typeName.equals(classOf[A].getName))
      }
    })

    Assert.assertEquals(2, staticFieldLoadCount)
  }

  @Test
  def staticFieldStoreTest(): Unit = {
    val opalSetup = new OpalSetup()
    opalSetup.setupOpal(classOf[FieldTarget].getName)

    val signature = new MethodSignature(classOf[FieldTarget].getName, "staticFieldStore", "Void")
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
        //Assert.assertTrue(typeName.equals("int") || typeName.equals(classOf[A].getName))
      }
    })

    Assert.assertEquals(2, staticFieldStoreCount)
  }

  @Test
  def innerFieldLoadTest(): Unit = {
    val opalSetup = new OpalSetup()
    opalSetup.setupOpal(classOf[FieldTarget].getName)

    val signature = new MethodSignature(classOf[FieldTarget].getName, "innerFieldLoad", "Void")
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
        Assert.assertTrue(fieldLoad.getX.getType.toString.equals(classOf[FieldClass.InnerFieldClass].getName))

        val fieldType = fieldLoad.getY.getType.toString
        Assert.assertFalse(fieldLoad.getY.isPredefinedField)
        //Assert.assertTrue(fieldType.equals("int") || fieldType.equals(classOf[A].getName))
      }
    })

    Assert.assertEquals(2, fieldLoadCount)
  }

  @Test
  def innerFieldStoreTest(): Unit = {
    val opalSetup = new OpalSetup()
    opalSetup.setupOpal(classOf[FieldTarget].getName)

    val signature = new MethodSignature(classOf[FieldTarget].getName, "innerFieldStore", "Void")
    val method = opalSetup.resolveMethod(signature)
    val opalMethod = OpalMethod(method)

    var fieldStoreCount = 0
    opalMethod.getStatements.forEach(stmt => {
      if (stmt.isFieldStore) {
        fieldStoreCount += 1

        val fieldStore = stmt.getFieldStore
        Assert.assertTrue(fieldStore.getX.getType.toString.equals(classOf[FieldClass.InnerFieldClass].getName))

        val fieldType = fieldStore.getY.getType.toString
        Assert.assertFalse(fieldStore.getY.isPredefinedField)
        Assert.assertTrue(fieldStore.getY.isInnerClassField)
        //Assert.assertTrue(fieldType.equals("int") || fieldType.equals(classOf[A].getName))
      }
    })

    Assert.assertEquals(2, fieldStoreCount)
  }
}
