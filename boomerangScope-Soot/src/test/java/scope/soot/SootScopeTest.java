package scope.soot;

import boomerang.scope.Method;
import boomerang.scope.soot.jimple.JimpleMethod;
import boomerang.scope.test.BoomerangScopeTests;
import boomerang.scope.test.MethodSignature;
import boomerang.scope.test.targets.A;
import boomerang.scope.test.targets.ParameterLocals;
import org.junit.Assert;
import org.junit.Test;
import soot.SootMethod;

import java.util.List;

public class SootScopeTest implements BoomerangScopeTests {

    @Test
    @Override
    public void parameterLocalTest() {
        SootSetup sootSetup = new SootSetup();
        sootSetup.setupSoot(ParameterLocals.class.getName());

        // No parameters
        MethodSignature noArgsSignature = new MethodSignature(ParameterLocals.class.getName(), "noParameters");
        SootMethod noArgs = sootSetup.resolveMethod(noArgsSignature);
        Method noArgsMethod = JimpleMethod.of(noArgs);

        Assert.assertTrue(noArgsMethod.getParameterLocals().isEmpty());

        // One parameter (primitive type)
        MethodSignature oneArgSignature = new MethodSignature(ParameterLocals.class.getName(), "oneParameter", List.of("int"));
        SootMethod oneArg = sootSetup.resolveMethod(oneArgSignature);
        Method oneArgMethod = JimpleMethod.of(oneArg);

        Assert.assertEquals(1, oneArgMethod.getParameterLocals().size());
        Assert.assertEquals("int", oneArgMethod.getParameterLocal(0).getType().toString());

        // Two parameters (primitive type + RefType)
        MethodSignature twoArgSignature = new MethodSignature(ParameterLocals.class.getName(), "twoParameters", List.of("int", A.class.getName()));
        SootMethod twoArgs = sootSetup.resolveMethod(twoArgSignature);
        Method twoArgsMethod = JimpleMethod.of(twoArgs);

        Assert.assertEquals(2, twoArgsMethod.getParameterLocals().size());
        Assert.assertEquals("int", twoArgsMethod.getParameterLocal(0).getType().toString());
        Assert.assertEquals(A.class.getName(), twoArgsMethod.getParameterLocal(1).getType().toString());
    }
}
