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
package boomerang.scope.soot;

import boomerang.scope.AllocVal;
import boomerang.scope.Method;
import boomerang.scope.Statement;
import boomerang.scope.Val;
import boomerang.scope.soot.jimple.JimpleMethod;
import boomerang.scope.test.MethodSignature;
import boomerang.scope.test.targets.A;
import boomerang.scope.test.targets.AllocValTarget;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import soot.Scene;
import soot.SootMethod;

/**
 * {@link AllocVal} is a decorator that forwards the {@link Val} interface to its delegate. The
 * locality checks {@link Val#isThisLocal()}, {@link Val#isReturnLocal()} and {@link
 * Val#isParameterLocal(int)} are implemented in {@link Val} in terms of {@code this}, so they only
 * hold for an AllocVal if the decorator forwards them too. Callers such as {@code
 * ForwardBoomerangSolver#applyCallSummary} branch on these checks; if they silently answer {@code
 * false} for a wrapped local, the corresponding call summary is dropped without any error.
 *
 * <p>The target method below has one of each kind of local:
 *
 * <pre>
 *   public A allocate(A)
 *   {
 *       A $stack3, allocated, a;
 *       AllocValTarget this;
 *
 *       this := @this: AllocValTarget;          // this local
 *       a := @parameter0: A;                    // parameter local 0
 *       $stack3 = new A;
 *       specialinvoke $stack3.&lt;A: void &lt;init&gt;()&gt;();
 *       allocated = $stack3;
 *       return allocated;                       // return local
 *   }
 * </pre>
 */
public class SootAllocValTest {

  private Method resolveAllocateMethod() {
    SootSetup sootSetup = new SootSetup();
    sootSetup.setupSoot(AllocValTarget.class.getName());

    MethodSignature signature =
        new MethodSignature(
            AllocValTarget.class.getName(),
            "allocate",
            A.class.getName(),
            List.of(A.class.getName()));
    SootMethod method = sootSetup.resolveMethod(signature);

    return JimpleMethod.of(method, Scene.v());
  }

  /** Wraps {@code delegate} the way the solvers do for an allocation of the value itself. */
  private AllocVal wrap(Val delegate, Method method) {
    Statement allocStatement = method.getStatements().get(0);
    return new AllocVal(delegate, allocStatement, delegate);
  }

  @Test
  public void thisLocalIsRecognizedThroughAllocVal() {
    Method method = resolveAllocateMethod();
    Val thisLocal = method.getThisLocal();

    Assertions.assertTrue(thisLocal.isThisLocal(), "precondition: the plain local is the this local");
    Assertions.assertTrue(
        wrap(thisLocal, method).isThisLocal(),
        "an AllocVal wrapping the this local must still report itself as the this local");
  }

  @Test
  public void parameterLocalIsRecognizedThroughAllocVal() {
    Method method = resolveAllocateMethod();
    Val parameterLocal = method.getParameterLocal(0);

    Assertions.assertTrue(
        parameterLocal.isParameterLocal(0), "precondition: the plain local is parameter 0");
    Assertions.assertTrue(
        wrap(parameterLocal, method).isParameterLocal(0),
        "an AllocVal wrapping a parameter local must still report itself as that parameter local");
  }

  @Test
  public void returnLocalIsRecognizedThroughAllocVal() {
    Method method = resolveAllocateMethod();
    Val returnLocal = method.getReturnLocals().iterator().next();

    Assertions.assertTrue(
        returnLocal.isReturnLocal(), "precondition: the plain local is a return local");
    Assertions.assertTrue(
        wrap(returnLocal, method).isReturnLocal(),
        "an AllocVal wrapping a return local must still report itself as a return local");
  }
}
