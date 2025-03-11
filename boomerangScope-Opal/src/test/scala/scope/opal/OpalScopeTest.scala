package scope.opal

import boomerang.scope.Method
import boomerang.scope.opal.tac.OpalMethod
import boomerang.scope.test.{BoomerangScopeTests, MethodSignature}
import boomerang.scope.test.targets.{A, ParameterLocals}
import org.junit.{Assert, Test}
import org.opalj.br.IntegerType

import java.util
import java.util.List

class OpalScopeTest extends BoomerangScopeTests {

  private val integerType = IntegerType.toJVMTypeName

  @Test
  override def parameterLocalTest(): Unit = {
    val opalSetup = new OpalSetup()
    opalSetup.setupOpal(classOf[ParameterLocals].getName)

    // No parameters
    val noArgsSignature = new MethodSignature(classOf[ParameterLocals].getName, "noParameters", "Void")
    val noArgs = opalSetup.resolveMethod(noArgsSignature)
    val noArgsMethod = OpalMethod(noArgs)

    Assert.assertTrue(noArgsMethod.getParameterLocals.isEmpty)

    // One parameter (primitive type)
    val oneArgSignature = new MethodSignature(classOf[ParameterLocals].getName, "oneParameter", "Void", util.List.of(integerType))
    val oneArg = opalSetup.resolveMethod(oneArgSignature)
    val oneArgMethod = OpalMethod(oneArg)

    Assert.assertEquals(1, oneArgMethod.getParameterLocals.size)
    Assert.assertEquals("int", oneArgMethod.getParameterLocal(0).getType.toString)

    // Two parameters (primitive type + RefType)
    val twoArgSignature = new MethodSignature(classOf[ParameterLocals].getName, "twoParameters", "Void", util.List.of(integerType, s"L${classOf[A].getName}L"))
    val twoArgs = opalSetup.resolveMethod(twoArgSignature)
    val twoArgsMethod = OpalMethod(twoArgs)

    Assert.assertEquals(2, twoArgsMethod.getParameterLocals.size)
    Assert.assertEquals("int", twoArgsMethod.getParameterLocal(0).getType.toString)
    Assert.assertEquals(classOf[A].getName, twoArgsMethod.getParameterLocal(1).getType.toString)
  }
}
