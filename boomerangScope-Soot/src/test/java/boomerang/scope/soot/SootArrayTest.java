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

import boomerang.scope.IArrayRef;
import boomerang.scope.Method;
import boomerang.scope.Statement;
import boomerang.scope.Val;
import boomerang.scope.soot.jimple.JimpleMethod;
import boomerang.scope.test.MethodSignature;
import boomerang.scope.test.targets.ArrayTarget;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import soot.Scene;
import soot.SootMethod;

public class SootArrayTest {

  @Test
  public void arrayStoreConstantTest() {
    SootSetup sootSetup = new SootSetup();
    sootSetup.setupSoot(ArrayTarget.class.getName());

    MethodSignature signature = new MethodSignature(ArrayTarget.class.getName(), "arrayStoreIndex");
    SootMethod method = sootSetup.resolveMethod(signature);
    Method jimpleMethod = JimpleMethod.of(method, Scene.v());

    int arrayStoreCount = 0;
    for (Statement stmt : jimpleMethod.getStatements()) {
      if (stmt.isArrayStore()) {
        arrayStoreCount++;

        IArrayRef arrayBase = stmt.getArrayBase();
        Assertions.assertFalse(arrayBase.getBase().isArrayRef());
        Assertions.assertTrue(arrayBase.getBase().isLocal());
        Assertions.assertEquals(0, arrayBase.getIndex());

        Val leftOp = stmt.getLeftOp();
        Assertions.assertTrue(leftOp.isArrayRef());
      }
    }

    Assertions.assertEquals(1, arrayStoreCount);
  }
}
