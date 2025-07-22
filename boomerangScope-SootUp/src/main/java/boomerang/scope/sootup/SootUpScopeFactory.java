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

import boomerang.scope.sootup.jimple.JimpleUpField;
import boomerang.scope.sootup.jimple.JimpleUpMethod;
import boomerang.scope.sootup.jimple.JimpleUpPhantomMethod;
import boomerang.scope.sootup.jimple.JimpleUpStatement;
import boomerang.scope.sootup.jimple.JimpleUpType;
import boomerang.scope.sootup.jimple.JimpleUpVal;
import boomerang.scope.sootup.jimple.JimpleUpWrappedClass;
import sootup.core.jimple.basic.Value;
import sootup.core.jimple.common.stmt.Stmt;
import sootup.core.signatures.FieldSignature;
import sootup.core.signatures.MethodSignature;
import sootup.core.types.ClassType;
import sootup.core.types.Type;
import sootup.java.core.JavaSootMethod;
import sootup.java.core.views.JavaView;

public class SootUpScopeFactory {

  public static JimpleUpField createJimpleUpField(FieldSignature signature) {
    return new JimpleUpField(signature);
  }

  public static JimpleUpMethod createJimpleUpMethod(JavaSootMethod method, JavaView view) {
    return JimpleUpMethod.of(method, view);
  }

  public static JimpleUpPhantomMethod createJimpleUpPhantomMethod(
      MethodSignature signature, JavaView view, boolean isStatic) {
    return JimpleUpPhantomMethod.of(signature, view, isStatic);
  }

  public static JimpleUpStatement createJimpleUpStatement(Stmt stmt, JimpleUpMethod method) {
    return JimpleUpStatement.create(stmt, method);
  }

  public static JimpleUpType createJimpleUpType(Type type, JavaView view) {
    return new JimpleUpType(type, view);
  }

  public static JimpleUpVal createJimpleUpVal(Value value, JimpleUpMethod method) {
    return new JimpleUpVal(value, method);
  }

  public static JimpleUpWrappedClass createJimpleUpWrappedClass(
      ClassType classType, JavaView view) {
    return new JimpleUpWrappedClass(classType, view);
  }
}
