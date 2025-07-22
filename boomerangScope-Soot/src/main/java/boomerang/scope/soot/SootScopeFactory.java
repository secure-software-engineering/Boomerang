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

import boomerang.scope.soot.jimple.JimpleField;
import boomerang.scope.soot.jimple.JimpleMethod;
import boomerang.scope.soot.jimple.JimplePhantomMethod;
import boomerang.scope.soot.jimple.JimpleStatement;
import boomerang.scope.soot.jimple.JimpleType;
import boomerang.scope.soot.jimple.JimpleVal;
import boomerang.scope.soot.jimple.JimpleWrappedClass;
import soot.Scene;
import soot.SootClass;
import soot.SootFieldRef;
import soot.SootMethod;
import soot.SootMethodRef;
import soot.Type;
import soot.Value;
import soot.jimple.Stmt;

public class SootScopeFactory {

  public static JimpleField createJimpleField(SootFieldRef fieldRef) {
    return new JimpleField(fieldRef);
  }

  public static JimpleMethod createJimpleMethod(SootMethod method, Scene scene) {
    return JimpleMethod.of(method, scene);
  }

  public static JimplePhantomMethod createJimplePhantomMethod(
      SootMethodRef methodRef, Scene scene) {
    return JimplePhantomMethod.of(methodRef, scene);
  }

  public static JimpleStatement createJimpleStatement(Stmt stmt, JimpleMethod method) {
    return JimpleStatement.create(stmt, method);
  }

  public static JimpleType createJimpleType(Type type, Scene scene) {
    return new JimpleType(type, scene);
  }

  public static JimpleVal createJimpleVal(Value value, JimpleMethod method) {
    return new JimpleVal(value, method);
  }

  public static JimpleWrappedClass createJimpleWrappedClass(SootClass sootClass, Scene scene) {
    return new JimpleWrappedClass(sootClass, scene);
  }
}
