/**
 * ***************************************************************************** 
 * Copyright (c) 2025 Fraunhofer IEM, Paderborn, Germany. This program and the
 * accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at http://www.eclipse.org/legal/epl-2.0.
 *
 * <p>SPDX-License-Identifier: EPL-2.0
 *
 * <p>Contributors: Johannes Spaeth - initial API and implementation
 * *****************************************************************************
 */
package boomerang.scope.soot;

import boomerang.scope.InvokeExpr;
import boomerang.scope.Method;
import boomerang.scope.Statement;
import boomerang.scope.Val;
import boomerang.scope.soot.jimple.JimpleMethod;
import boomerang.scope.test.MethodSignature;
import boomerang.scope.test.targets.A;
import boomerang.scope.test.targets.HashCodeEqualsLocalTarget;
import boomerang.scope.test.targets.ParameterLocalsTarget;
import boomerang.scope.test.targets.ThisLocalTarget;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import soot.SootMethod;

public class SootScopeTest {

  @Test
  public void thisLocalTest() {
    SootSetup sootSetup = new SootSetup();
    sootSetup.setupSoot(ThisLocalTarget.class.getName());

    MethodSignature signature = new MethodSignature(ThisLocalTarget.class.getName(), "call");
    SootMethod method = sootSetup.resolveMethod(signature);
    Method jimpleMethod = JimpleMethod.of(method);

    boolean checked = false;
    for (Statement stmt : jimpleMethod.getStatements()) {
      if (stmt.containsInvokeExpr()
          && stmt.getInvokeExpr().getMethod().getName().equals("callWithThis")) {
        InvokeExpr invokeExpr = stmt.getInvokeExpr();
        Val base = invokeExpr.getBase();

        Assert.assertEquals(jimpleMethod.getThisLocal(), base);
        Assert.assertEquals(base, jimpleMethod.getThisLocal());
        Assert.assertTrue(jimpleMethod.isThisLocal(base));

        checked = true;
      }
    }

    if (!checked) {
      Assert.fail("Did not check this local");
    }
  }

  @Test
  public void parameterLocalTest() {
    SootSetup sootSetup = new SootSetup();
    sootSetup.setupSoot(ParameterLocalsTarget.class.getName());

    // No parameters
    MethodSignature noArgsSignature =
        new MethodSignature(ParameterLocalsTarget.class.getName(), "noParameters");
    SootMethod noArgs = sootSetup.resolveMethod(noArgsSignature);
    Method noArgsMethod = JimpleMethod.of(noArgs);

    Assert.assertTrue(noArgsMethod.getParameterLocals().isEmpty());

    // One parameter (primitive type)
    MethodSignature oneArgSignature =
        new MethodSignature(ParameterLocalsTarget.class.getName(), "oneParameter", List.of("int"));
    SootMethod oneArg = sootSetup.resolveMethod(oneArgSignature);
    Method oneArgMethod = JimpleMethod.of(oneArg);

    Assert.assertEquals(1, oneArgMethod.getParameterLocals().size());
    Assert.assertEquals("int", oneArgMethod.getParameterLocal(0).getType().toString());

    // Two parameters (primitive type + RefType)
    MethodSignature twoArgSignature =
        new MethodSignature(
            ParameterLocalsTarget.class.getName(),
            "twoParameters",
            List.of("int", A.class.getName()));
    SootMethod twoArgs = sootSetup.resolveMethod(twoArgSignature);
    Method twoArgsMethod = JimpleMethod.of(twoArgs);

    Assert.assertEquals(2, twoArgsMethod.getParameterLocals().size());
    Assert.assertEquals("int", twoArgsMethod.getParameterLocal(0).getType().toString());
    Assert.assertEquals(A.class.getName(), twoArgsMethod.getParameterLocal(1).getType().toString());
  }

  @Test
  public void hashCodeEqualsLocalTest() {
    SootSetup sootSetup = new SootSetup();
    sootSetup.setupSoot(HashCodeEqualsLocalTarget.class.getName());

    // Parameter locals
    MethodSignature signature =
        new MethodSignature(
            ParameterLocalsTarget.class.getName(),
            "parameterCall",
            List.of(A.class.getName(), "int"));
    SootMethod method = sootSetup.resolveMethod(signature);
    Method jimpleMethod = JimpleMethod.of(method);

    Val firstArg = jimpleMethod.getParameterLocal(0);
    Val secondArg = jimpleMethod.getParameterLocal(1);

    boolean checked = false;
    for (Statement stmt : jimpleMethod.getStatements()) {
      if (stmt.containsInvokeExpr()
          && stmt.getInvokeExpr().getMethod().getName().equals("methodCall")) {
        InvokeExpr invokeExpr = stmt.getInvokeExpr();
        Val base = invokeExpr.getBase();
        Val arg = invokeExpr.getArg(0);

        // equals in both directions
        Assert.assertEquals(base, firstArg);
        Assert.assertEquals(firstArg, base);

        Assert.assertEquals(arg, secondArg);
        Assert.assertEquals(secondArg, arg);

        // hash codes
        Assert.assertEquals(base.hashCode(), firstArg.hashCode());
        Assert.assertEquals(arg.hashCode(), secondArg.hashCode());

        checked = true;
      }
    }

    if (!checked) {
      Assert.fail("Did not check equals and hashCode methods for parameter locals");
    }
  }
}
