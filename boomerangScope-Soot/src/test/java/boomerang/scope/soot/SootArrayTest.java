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
import boomerang.scope.soot.jimple.JimpleArrayRef;
import boomerang.scope.soot.jimple.JimpleMethod;
import boomerang.scope.test.MethodSignature;
import boomerang.scope.test.targets.ArrayTarget;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import soot.ArrayType;
import soot.IntType;
import soot.Local;
import soot.Scene;
import soot.SootMethod;
import soot.jimple.IntConstant;
import soot.jimple.Jimple;

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

  @Test
  public void arrayRefEqualsAndHashCodeTest() {
    SootSetup sootSetup = new SootSetup();
    sootSetup.setupSoot(ArrayTarget.class.getName());

    MethodSignature signature = new MethodSignature(ArrayTarget.class.getName(), "arrayStoreIndex");
    SootMethod method = sootSetup.resolveMethod(signature);
    JimpleMethod jimpleMethod = JimpleMethod.of(method, Scene.v());

    // Soot locals are unique per body, i.e. the same access uses the same local instances
    Local a = Jimple.v().newLocal("a", ArrayType.v(IntType.v(), 1));
    Local b = Jimple.v().newLocal("b", ArrayType.v(IntType.v(), 1));
    Local i = Jimple.v().newLocal("i", IntType.v());

    // Different instances of the same array access (e.g. from different statements)
    Val ref1 = new JimpleArrayRef(Jimple.v().newArrayRef(a, IntConstant.v(0)), jimpleMethod);
    Val ref2 = new JimpleArrayRef(Jimple.v().newArrayRef(a, IntConstant.v(0)), jimpleMethod);
    Assertions.assertEquals(ref1, ref2);
    Assertions.assertEquals(ref1.hashCode(), ref2.hashCode());
    Assertions.assertEquals(Set.of(ref1), new HashSet<>(List.of(ref1, ref2)));

    Val localIndex1 = new JimpleArrayRef(Jimple.v().newArrayRef(a, i), jimpleMethod);
    Val localIndex2 = new JimpleArrayRef(Jimple.v().newArrayRef(a, i), jimpleMethod);
    Assertions.assertEquals(localIndex1, localIndex2);

    Assertions.assertNotEquals(
        ref1, new JimpleArrayRef(Jimple.v().newArrayRef(b, IntConstant.v(0)), jimpleMethod));
    Assertions.assertNotEquals(
        ref1, new JimpleArrayRef(Jimple.v().newArrayRef(a, IntConstant.v(1)), jimpleMethod));
    Assertions.assertNotEquals(ref1, localIndex1);
  }
}
