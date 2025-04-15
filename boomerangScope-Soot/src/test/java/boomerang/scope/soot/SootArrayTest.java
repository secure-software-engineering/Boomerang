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

import boomerang.scope.Method;
import boomerang.scope.Pair;
import boomerang.scope.Statement;
import boomerang.scope.Val;
import boomerang.scope.soot.jimple.JimpleMethod;
import boomerang.scope.test.MethodSignature;
import boomerang.scope.test.targets.ArrayTarget;
import org.junit.Assert;
import org.junit.Test;
import soot.SootMethod;

public class SootArrayTest {

  @Test
  public void singleArrayStoreConstantTest() {
    SootSetup sootSetup = new SootSetup();
    sootSetup.setupSoot(ArrayTarget.class.getName());

    MethodSignature signature =
        new MethodSignature(ArrayTarget.class.getName(), "singleArrayStoreIndex");
    SootMethod method = sootSetup.resolveMethod(signature);
    Method jimpleMethod = JimpleMethod.of(method);

    int arrayStoreCount = 0;
    for (Statement stmt : jimpleMethod.getStatements()) {
      if (stmt.isArrayStore()) {
        arrayStoreCount++;

        Pair<Val, Integer> arrayBase = stmt.getArrayBase();
        Assert.assertFalse(arrayBase.getX().isArrayRef());
        Assert.assertTrue(arrayBase.getX().isLocal());
        Assert.assertEquals(0, arrayBase.getY().intValue());

        Val leftOp = stmt.getLeftOp();
        Assert.assertTrue(leftOp.isArrayRef());
      }
    }

    Assert.assertEquals(1, arrayStoreCount);
  }
}
