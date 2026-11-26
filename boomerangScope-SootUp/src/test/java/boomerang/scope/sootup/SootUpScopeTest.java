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
import boomerang.scope.sootup.jimple.JimpleUpMethod;
import boomerang.scope.sootup.jimple.JimpleUpPhantomMethod;
import boomerang.scope.sootup.jimple.JimpleUpWrappedClass;
import boomerang.scope.test.targets.ScopeTarget;
import boomerang.scope.test.targets.StartPointTarget;
import java.util.Collection;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import sootup.core.jimple.common.Value;
import sootup.core.jimple.common.stmt.JIdentityStmt;
import sootup.core.jimple.common.stmt.JNopStmt;
import sootup.core.jimple.common.stmt.Stmt;
import sootup.core.model.Body;
import sootup.core.signatures.FieldSignature;
import sootup.core.signatures.MethodSignature;
import sootup.core.types.ClassType;
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
  public void identityStmtIsTheStartPointAndHandlersAreNot() {
    SootUpSetup sootUpSetup = new SootUpSetup();
    sootUpSetup.setupSootUp(StartPointTarget.class.getName());

    boomerang.scope.test.MethodSignature signature =
        new boomerang.scope.test.MethodSignature(
            StartPointTarget.class.getName(), "methodWithHandler", "int", List.of("int"));
    JavaSootMethod method = sootUpSetup.resolveMethod(signature);
    JimpleUpMethod jimpleMethod =
        SootUpScopeConverter.createJimpleUpMethod(method, sootUpSetup.getJavaView());

    // the body starts with this := @this, and the handler starts with $e := @caughtexception
    Collection<Statement> startPoints = jimpleMethod.getControlFlowGraph().getStartPoints();
    Assertions.assertEquals(1, startPoints.size());
    Assertions.assertEquals(
        method.getBody().getControlFlowGraph().getStartingStmt(),
        SootUpScopeConverter.extractSootUpStatement(startPoints.iterator().next()));
  }

  @Test
  public void leadingNopOnlyForBodiesWithoutIdentityStmts() {
    SootUpSetup sootUpSetup = new SootUpSetup();
    sootUpSetup.setupSootUp(StartPointTarget.class.getName());
    JavaView view = sootUpSetup.getJavaView();

    JavaSootMethod instanceMethod =
        sootUpSetup.resolveMethod(
            new boomerang.scope.test.MethodSignature(
                StartPointTarget.class.getName(), "methodWithHandler", "int", List.of("int")));
    Assertions.assertInstanceOf(
        JIdentityStmt.class, firstStmtAfterPreInterceptor(instanceMethod, view));

    JavaSootMethod staticMethod =
        sootUpSetup.resolveMethod(
            new boomerang.scope.test.MethodSignature(
                StartPointTarget.class.getName(), "delegate", "int"));
    Assertions.assertInstanceOf(JNopStmt.class, firstStmtAfterPreInterceptor(staticMethod, view));
  }

  private static Stmt firstStmtAfterPreInterceptor(JavaSootMethod method, JavaView view) {
    Body.BodyBuilder builder = Body.builder(method.getBody(), method.getModifiers());
    new BoomerangPreInterceptor().interceptBody(builder, view);
    return builder.getControlFlowGraph().getStartingStmt();
  }
}
