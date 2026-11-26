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
package boomerang.scope.sootup;

import boomerang.scope.Field;
import boomerang.scope.Statement;
import boomerang.scope.Type;
import boomerang.scope.Val;
import boomerang.scope.sootup.jimple.JimpleUpArrayRef;
import boomerang.scope.sootup.jimple.JimpleUpInstanceFieldRef;
import boomerang.scope.sootup.jimple.JimpleUpMethod;
import boomerang.scope.sootup.jimple.JimpleUpPhantomMethod;
import boomerang.scope.sootup.jimple.JimpleUpStaticFieldRef;
import boomerang.scope.sootup.jimple.JimpleUpWrappedClass;
import boomerang.scope.test.targets.ScopeTarget;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import sootup.core.jimple.common.Local;
import sootup.core.jimple.common.Value;
import sootup.core.jimple.common.constant.IntConstant;
import sootup.core.jimple.common.ref.JArrayRef;
import sootup.core.jimple.common.ref.JInstanceFieldRef;
import sootup.core.jimple.common.ref.JStaticFieldRef;
import sootup.core.jimple.common.stmt.Stmt;
import sootup.core.signatures.FieldSignature;
import sootup.core.signatures.MethodSignature;
import sootup.core.types.ArrayType;
import sootup.core.types.ClassType;
import sootup.core.types.PrimitiveType;
import sootup.java.core.JavaSootMethod;
import sootup.java.core.views.JavaView;

public class SootUpScopeTest {

  @Test
  public void testFactoryAndConverter() {
    SootUpSetup sootUpSetup = new SootUpSetup();
    sootUpSetup.setupSootUp(ScopeTarget.class.getName());

    boomerang.scope.test.MethodSignature signature =
        new boomerang.scope.test.MethodSignature(
            ScopeTarget.class.getName(), "methodWithStatements", "int");
    JavaSootMethod method = sootUpSetup.resolveMethod(signature);
    JavaView view = sootUpSetup.getJavaView();

    // Test method factory and converter
    JimpleUpMethod jimpleMethod = SootUpScopeConverter.createJimpleUpMethod(method, view);
    JavaSootMethod sootMethod = SootUpScopeConverter.extractSootUpMethod(jimpleMethod);
    Assertions.assertEquals(method, sootMethod);

    // Test phantom method factory and converter
    JimpleUpPhantomMethod phantomMethod =
        SootUpScopeConverter.createJimpleUpPhantomMethod(method.getSignature(), view, true);
    MethodSignature methodRef = SootUpScopeConverter.extractSootUpMethodSignature(phantomMethod);
    Assertions.assertEquals(method.getSignature(), methodRef);

    // Test class factory and converter
    ClassType sootClass = method.getDeclaringClassType();
    JimpleUpWrappedClass wrappedClass =
        SootUpScopeConverter.createJimpleUpWrappedClass(sootClass, view);
    ClassType newSootClass = SootUpScopeConverter.extractSootUpClass(wrappedClass);
    Assertions.assertEquals(sootClass, newSootClass);

    boolean checkedField = false;
    boolean checkedType = false;
    boolean checkedVal = false;

    for (Statement statement : jimpleMethod.getStatements()) {
      // Test statement factory and converter
      Stmt sootStmt = SootUpScopeConverter.extractSootUpStatement(statement);
      Statement newStmt = SootUpScopeConverter.createJimpleUpStatement(sootStmt, jimpleMethod);
      Assertions.assertEquals(statement, newStmt);

      // Test field factory and converter
      if (statement.isFieldLoad()) {
        Field field = statement.getLoadedField();
        FieldSignature fieldRef = SootUpScopeConverter.extractSootUpFieldSignature(field);
        Field newField = SootUpScopeConverter.createJimpleUpField(fieldRef);
        Assertions.assertEquals(field, newField);

        checkedField = true;
      }

      // Test type factory and converter
      if (statement.isAssignStmt() && statement.getRightOp().isNewExpr()) {
        Type type = statement.getRightOp().getNewExprType();
        sootup.core.types.Type sootType = SootUpScopeConverter.extractSootUpType(type);
        Type newType = SootUpScopeConverter.createJimpleUpType(sootType, view);
        Assertions.assertEquals(type, newType);

        checkedType = true;
      }

      // Test val factory and converter
      if (statement.isAssignStmt() && statement.getRightOp().isIntConstant()) {
        Val rightOp = statement.getRightOp();
        Value value = SootUpScopeConverter.extractSootUpValue(rightOp);
        Val newRightOp = SootUpScopeConverter.createJimpleUpVal(value, jimpleMethod);
        Assertions.assertEquals(rightOp, newRightOp);

        checkedVal = true;
      }
    }

    Assertions.assertTrue(checkedField);
    Assertions.assertTrue(checkedType);
    Assertions.assertTrue(checkedVal);
  }

  @Test
  public void staticFieldRefEqualsAndHashCode() {
    SootUpSetup sootUpSetup = new SootUpSetup();
    sootUpSetup.setupSootUp(ScopeTarget.class.getName());

    boomerang.scope.test.MethodSignature signature =
        new boomerang.scope.test.MethodSignature(
            ScopeTarget.class.getName(), "methodWithStatements", "int");
    JavaSootMethod method = sootUpSetup.resolveMethod(signature);
    JavaView view = sootUpSetup.getJavaView();
    JimpleUpMethod jimpleMethod = SootUpScopeConverter.createJimpleUpMethod(method, view);

    FieldSignature fieldSignature =
        view.getIdentifierFactory()
            .getFieldSignature("staticField", method.getDeclaringClassType(), "int");

    // Different instances of the same static field reference (e.g. from different statements)
    Val ref1 = new JimpleUpStaticFieldRef(new JStaticFieldRef(fieldSignature), jimpleMethod);
    Val ref2 = new JimpleUpStaticFieldRef(new JStaticFieldRef(fieldSignature), jimpleMethod);
    Assertions.assertEquals(ref1, ref2);
    Assertions.assertEquals(ref1.hashCode(), ref2.hashCode());
    Assertions.assertEquals(Set.of(ref1), new HashSet<>(List.of(ref1, ref2)));

    FieldSignature otherSignature =
        view.getIdentifierFactory()
            .getFieldSignature("otherStaticField", method.getDeclaringClassType(), "int");
    Val other = new JimpleUpStaticFieldRef(new JStaticFieldRef(otherSignature), jimpleMethod);
    Assertions.assertNotEquals(ref1, other);
  }

  @Test
  public void instanceFieldRefEqualsAndHashCode() {
    SootUpSetup sootUpSetup = new SootUpSetup();
    sootUpSetup.setupSootUp(ScopeTarget.class.getName());

    boomerang.scope.test.MethodSignature signature =
        new boomerang.scope.test.MethodSignature(
            ScopeTarget.class.getName(), "methodWithStatements", "int");
    JavaSootMethod method = sootUpSetup.resolveMethod(signature);
    JavaView view = sootUpSetup.getJavaView();
    JimpleUpMethod jimpleMethod = SootUpScopeConverter.createJimpleUpMethod(method, view);

    ClassType classType = method.getDeclaringClassType();
    FieldSignature field = view.getIdentifierFactory().getFieldSignature("field", classType, "int");
    FieldSignature otherField =
        view.getIdentifierFactory().getFieldSignature("otherField", classType, "int");
    Local a = new Local("a", classType);
    Local b = new Local("b", classType);

    // Different instances of the same field reference (e.g. from different statements)
    Val ref1 = new JimpleUpInstanceFieldRef(new JInstanceFieldRef(a, field), jimpleMethod);
    Val ref2 =
        new JimpleUpInstanceFieldRef(
            new JInstanceFieldRef(new Local("a", classType), field), jimpleMethod);
    Assertions.assertEquals(ref1, ref2);
    Assertions.assertEquals(ref1.hashCode(), ref2.hashCode());
    Assertions.assertEquals(Set.of(ref1), new HashSet<>(List.of(ref1, ref2)));

    // SootUp's JInstanceFieldRef#equals ignores the base
    Val otherBase = new JimpleUpInstanceFieldRef(new JInstanceFieldRef(b, field), jimpleMethod);
    Assertions.assertNotEquals(ref1, otherBase);

    Val otherFieldRef =
        new JimpleUpInstanceFieldRef(new JInstanceFieldRef(a, otherField), jimpleMethod);
    Assertions.assertNotEquals(ref1, otherFieldRef);
  }

  @Test
  public void arrayRefEqualsAndHashCode() {
    SootUpSetup sootUpSetup = new SootUpSetup();
    sootUpSetup.setupSootUp(ScopeTarget.class.getName());

    boomerang.scope.test.MethodSignature signature =
        new boomerang.scope.test.MethodSignature(
            ScopeTarget.class.getName(), "methodWithStatements", "int");
    JavaSootMethod method = sootUpSetup.resolveMethod(signature);
    JavaView view = sootUpSetup.getJavaView();
    JimpleUpMethod jimpleMethod = SootUpScopeConverter.createJimpleUpMethod(method, view);

    ArrayType arrayType = view.getIdentifierFactory().getArrayType(PrimitiveType.getInt(), 1);
    Local a = new Local("a", arrayType);
    Local b = new Local("b", arrayType);
    Local i = new Local("i", PrimitiveType.getInt());

    // Different instances of the same array access (e.g. from different statements)
    Val ref1 = new JimpleUpArrayRef(new JArrayRef(a, IntConstant.getInstance(0)), jimpleMethod);
    Val ref2 =
        new JimpleUpArrayRef(
            new JArrayRef(new Local("a", arrayType), IntConstant.getInstance(0)), jimpleMethod);
    Assertions.assertEquals(ref1, ref2);
    Assertions.assertEquals(ref1.hashCode(), ref2.hashCode());
    Assertions.assertEquals(Set.of(ref1), new HashSet<>(List.of(ref1, ref2)));

    Val localIndex1 = new JimpleUpArrayRef(new JArrayRef(a, i), jimpleMethod);
    Val localIndex2 =
        new JimpleUpArrayRef(
            new JArrayRef(a, new Local("i", PrimitiveType.getInt())), jimpleMethod);
    Assertions.assertEquals(localIndex1, localIndex2);

    Val otherBase =
        new JimpleUpArrayRef(new JArrayRef(b, IntConstant.getInstance(0)), jimpleMethod);
    Assertions.assertNotEquals(ref1, otherBase);
    Val otherIndex =
        new JimpleUpArrayRef(new JArrayRef(a, IntConstant.getInstance(1)), jimpleMethod);
    Assertions.assertNotEquals(ref1, otherIndex);
    Assertions.assertNotEquals(ref1, localIndex1);
  }
}
