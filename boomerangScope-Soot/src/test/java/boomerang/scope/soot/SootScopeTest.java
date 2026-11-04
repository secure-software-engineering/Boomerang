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

import boomerang.scope.Field;
import boomerang.scope.Statement;
import boomerang.scope.Type;
import boomerang.scope.Val;
import boomerang.scope.soot.jimple.JimpleMethod;
import boomerang.scope.soot.jimple.JimplePhantomMethod;
import boomerang.scope.soot.jimple.JimpleWrappedClass;
import boomerang.scope.test.MethodSignature;
import boomerang.scope.test.targets.ScopeTarget;
import boomerang.scope.test.targets.StartPointTarget;
import java.util.Collection;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import soot.Body;
import soot.Scene;
import soot.SootClass;
import soot.SootFieldRef;
import soot.SootMethod;
import soot.SootMethodRef;
import soot.Unit;
import soot.Value;
import soot.jimple.IdentityStmt;
import soot.jimple.NopStmt;
import soot.jimple.Stmt;

public class SootScopeTest {

  @Test
  public void testFactoryAndConverter() {
    SootSetup sootSetup = new SootSetup();
    sootSetup.setupSoot(ScopeTarget.class.getName());

    MethodSignature signature =
        new MethodSignature(ScopeTarget.class.getName(), "methodWithStatements", "int");
    SootMethod method = sootSetup.resolveMethod(signature);

    // Test method factory and converter
    JimpleMethod jimpleMethod = SootScopeConverter.createJimpleMethod(method, Scene.v());
    SootMethod sootMethod = SootScopeConverter.extractSootMethod(jimpleMethod);
    Assertions.assertEquals(method, sootMethod);

    // Test phantom method factory and converter
    JimplePhantomMethod phantomMethod =
        SootScopeConverter.createJimplePhantomMethod(method.makeRef(), Scene.v());
    SootMethodRef methodRef = SootScopeConverter.extractSootMethodRef(phantomMethod);
    Assertions.assertEquals(method.makeRef(), methodRef);

    // Test class factory and converter
    SootClass sootClass = method.getDeclaringClass();
    JimpleWrappedClass wrappedClass =
        SootScopeConverter.createJimpleWrappedClass(sootClass, Scene.v());
    SootClass newSootClass = SootScopeConverter.extractSootClass(wrappedClass);
    Assertions.assertEquals(sootClass, newSootClass);

    boolean checkedField = false;
    boolean checkedType = false;
    boolean checkedVal = false;

    for (Statement statement : jimpleMethod.getStatements()) {
      // Test statement factory and converter
      Stmt sootStmt = SootScopeConverter.extractSootStatement(statement);
      Statement newStmt = SootScopeConverter.createJimpleStatement(sootStmt, jimpleMethod);
      Assertions.assertEquals(statement, newStmt);

      // Test field factory and converter
      if (statement.isFieldLoad()) {
        Field field = statement.getLoadedField();
        SootFieldRef fieldRef = SootScopeConverter.extractSootFieldRef(field);
        Field newField = SootScopeConverter.createJimpleField(fieldRef);
        Assertions.assertEquals(field, newField);

        checkedField = true;
      }

      // Test type factory and converter
      if (statement.isAssignStmt() && statement.getRightOp().isNewExpr()) {
        Type type = statement.getRightOp().getNewExprType();
        soot.Type sootType = SootScopeConverter.extractSootType(type);
        Type newType = SootScopeConverter.createJimpleType(sootType, Scene.v());
        Assertions.assertEquals(type, newType);

        checkedType = true;
      }

      // Test val factory and converter
      if (statement.isAssignStmt() && statement.getRightOp().isIntConstant()) {
        Val rightOp = statement.getRightOp();
        Value value = SootScopeConverter.extractSootValue(rightOp);
        Val newRightOp = SootScopeConverter.createJimpleVal(value, jimpleMethod);
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
    SootSetup sootSetup = new SootSetup();
    sootSetup.setupSoot(StartPointTarget.class.getName());

    MethodSignature signature =
        new MethodSignature(
            StartPointTarget.class.getName(), "methodWithHandler", "int", List.of("int"));
    SootMethod method = sootSetup.resolveMethod(signature);
    method.retrieveActiveBody();
    JimpleMethod jimpleMethod = SootScopeConverter.createJimpleMethod(method, Scene.v());

    // the body starts with this := @this, and the handler starts with $e := @caughtexception
    Collection<Statement> startPoints = jimpleMethod.getControlFlowGraph().getStartPoints();
    Assertions.assertEquals(1, startPoints.size());
    Assertions.assertEquals(
        method.getActiveBody().getUnits().getFirst(),
        SootScopeConverter.extractSootStatement(startPoints.iterator().next()));
  }

  @Test
  public void leadingNopOnlyForBodiesWithoutIdentityStmts() {
    SootSetup sootSetup = new SootSetup();
    sootSetup.setupSoot(StartPointTarget.class.getName());

    SootMethod instanceMethod =
        sootSetup.resolveMethod(
            new MethodSignature(
                StartPointTarget.class.getName(), "methodWithHandler", "int", List.of("int")));
    Assertions.assertInstanceOf(IdentityStmt.class, firstUnitAfterPretransformer(instanceMethod));

    SootMethod staticMethod =
        sootSetup.resolveMethod(
            new MethodSignature(StartPointTarget.class.getName(), "delegate", "int"));
    Assertions.assertInstanceOf(NopStmt.class, firstUnitAfterPretransformer(staticMethod));
  }

  private static Unit firstUnitAfterPretransformer(SootMethod method) {
    Body body = method.retrieveActiveBody();
    BoomerangPretransformer.v().transform(body);
    return body.getUnits().getFirst();
  }
}
