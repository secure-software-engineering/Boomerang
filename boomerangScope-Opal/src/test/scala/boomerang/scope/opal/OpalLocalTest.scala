package boomerang.scope.opal

import boomerang.scope.Statement
import boomerang.scope.opal.tac.OpalMethod
import boomerang.scope.test.targets.{A, HashCodeEqualsLocalTarget, LocalCountTarget, ParameterLocalsTarget, ThisLocalTarget}
import boomerang.scope.test.MethodSignature
import org.junit.{Assert, Test}
import org.opalj.br.IntegerType

import java.util

class OpalLocalTest {

  private val integerType = IntegerType.toJVMTypeName

  @Test
  def thisLocalTest(): Unit = {
    val opalSetup = new OpalSetup()
    opalSetup.setupOpal(classOf[ThisLocalTarget].getName)

    val signature = new MethodSignature(classOf[ThisLocalTarget].getName, "call", "Void")
    val method = opalSetup.resolveMethod(signature)
    val opalMethod = OpalMethod(method)

    var checked = false
    opalMethod.getStatements.forEach(stmt => {
      if (stmt.containsInvokeExpr() && stmt.getInvokeExpr.getMethod.getName.equals("callWithThis")) {
        val invokeExpr = stmt.getInvokeExpr
        val base = invokeExpr.getBase

        Assert.assertTrue(opalMethod.getThisLocal.equals(base))
        Assert.assertTrue(base.equals(opalMethod.getThisLocal))
        Assert.assertTrue(opalMethod.isThisLocal(base))

        checked = true
      }
    })

    if (!checked) {
      Assert.fail("Did not check this local")
    }
  }

  @Test
  def localCountTest(): Unit = {
    val opalSetup = new OpalSetup()
    opalSetup.setupOpal(classOf[LocalCountTarget].getName)

    // Virtual
    val virtualSignature = new MethodSignature(classOf[LocalCountTarget].getName, "virtualLocalCount", "Void", util.List.of(integerType, s"L${classOf[A].getName}L"))
    val virtualMethod = opalSetup.resolveMethod(virtualSignature)
    val virtualOpalMethod = OpalMethod(virtualMethod)

    val locals = virtualOpalMethod.getLocals
    Assert.assertEquals(5, locals.size())

    // Static
    val staticSignature = new MethodSignature(classOf[LocalCountTarget].getName, "staticLocalCount", "Void", util.List.of(integerType, s"L${classOf[A].getName}L"))
    val staticMethod = opalSetup.resolveMethod(staticSignature)
    val staticOpalMethod = OpalMethod(staticMethod)

    val locals2 = staticOpalMethod.getLocals
    Assert.assertEquals(4, locals2.size())
  }

  @Test
  def parameterLocalTest(): Unit = {
    val opalSetup = new OpalSetup()
    opalSetup.setupOpal(classOf[ParameterLocalsTarget].getName)

    // No parameters
    val noArgsSignature = new MethodSignature(classOf[ParameterLocalsTarget].getName, "noParameters", "Void")
    val noArgs = opalSetup.resolveMethod(noArgsSignature)
    val noArgsMethod = OpalMethod(noArgs)

    Assert.assertTrue(noArgsMethod.getParameterLocals.isEmpty)

    // One parameter (primitive type)
    val oneArgSignature = new MethodSignature(classOf[ParameterLocalsTarget].getName, "oneParameter", "Void", util.List.of(integerType))
    val oneArg = opalSetup.resolveMethod(oneArgSignature)
    val oneArgMethod = OpalMethod(oneArg)

    Assert.assertEquals(1, oneArgMethod.getParameterLocals.size)
    //Assert.assertEquals("int", oneArgMethod.getParameterLocal(0).getType.toString)

    // Two parameters (primitive type + RefType)
    val twoArgSignature = new MethodSignature(classOf[ParameterLocalsTarget].getName, "twoParameters", "Void", util.List.of(integerType, s"L${classOf[A].getName}L"))
    val twoArgs = opalSetup.resolveMethod(twoArgSignature)
    val twoArgsMethod = OpalMethod(twoArgs)

    Assert.assertEquals(2, twoArgsMethod.getParameterLocals.size)
    //Assert.assertEquals("int", twoArgsMethod.getParameterLocal(0).getType.toString)
    //Assert.assertEquals(classOf[A].getName, twoArgsMethod.getParameterLocal(1).getType.toString)
  }

  @Test
  def hashCodeEqualsLocalTest(): Unit = {
    val opalSetup = new OpalSetup()
    opalSetup.setupOpal(classOf[HashCodeEqualsLocalTarget].getName)

    // Parameter locals
    val signature = new MethodSignature(classOf[HashCodeEqualsLocalTarget].getName, "parameterCall", "Void", util.List.of(s"L${classOf[A].getName}L", integerType))
    val method = opalSetup.resolveMethod(signature)
    val opalMethod = OpalMethod(method)

    val firstArg = opalMethod.getParameterLocal(0)
    val secondArg = opalMethod.getParameterLocal(1)

    var checked = false
    opalMethod.getStatements.forEach(stmt => {
      if (stmt.containsInvokeExpr() && stmt.getInvokeExpr.getMethod.getName.equals("methodCall")) {
        val invokeExpr = stmt.getInvokeExpr
        val base = invokeExpr.getBase
        val arg = invokeExpr.getArg(0)

        // equals in both directions
        Assert.assertTrue(base.equals(firstArg))
        Assert.assertTrue(firstArg.equals(base))

        Assert.assertTrue(arg.equals(secondArg))
        Assert.assertTrue(secondArg.equals(arg))

        // hash codes
        Assert.assertEquals(base.hashCode, firstArg.hashCode)
        Assert.assertEquals(arg.hashCode, secondArg.hashCode)

        checked = true
      }
    })

    if (!checked) {
      Assert.fail("Did not checked equals and hashCode methods")
    }

    // Defined locals
    val signature2 = new MethodSignature(classOf[ParameterLocalsTarget].getName, "definedCall", "Void")
    val method2 = opalSetup.resolveMethod(signature2)
    val jimpleMethod2 = OpalMethod(method2)

    // Find the definition sites
    val refDefStmt = jimpleMethod2.getStatements.stream.filter((stmt: Statement) => stmt.isAssignStmt && stmt.getRightOp.isNewExpr).findFirst
    val primDefStmt= jimpleMethod2.getStatements.stream.filter((stmt: Statement) => stmt.isAssignStmt && stmt.getRightOp.isIntConstant).findFirst

    if (refDefStmt.isEmpty || primDefStmt.isEmpty) {
      Assert.fail("Could not find def statement")
    }

    val refDefLocal = refDefStmt.get.getLeftOp
    val primDefLocal = primDefStmt.get.getLeftOp

    var checked2: Boolean = false
    jimpleMethod2.getStatements.forEach(stmt => {
      if (stmt.containsInvokeExpr && stmt.getInvokeExpr.getMethod.getName == "methodCall") {
        val invokeExpr = stmt.getInvokeExpr
        val base = invokeExpr.getBase
        val arg = invokeExpr.getArg(0)

        // equals in both directions
        Assert.assertEquals(base, refDefLocal)
        Assert.assertEquals(refDefLocal, base)

        Assert.assertEquals(arg, primDefLocal)
        Assert.assertEquals(primDefLocal, arg)

        // hash codes
        Assert.assertEquals(base.hashCode, refDefLocal.hashCode)
        Assert.assertEquals(arg.hashCode, primDefLocal.hashCode)

        checked2 = true
      }
    })

    if (!checked2) {
      Assert.fail("Did not check equals and hashCode methods for defined locals")
    }
  }
}
