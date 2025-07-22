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
import boomerang.scope.Method;
import boomerang.scope.PhantomMethod;
import boomerang.scope.Statement;
import boomerang.scope.Type;
import boomerang.scope.Val;
import boomerang.scope.WrappedClass;
import boomerang.scope.soot.jimple.JimpleField;
import boomerang.scope.soot.jimple.JimpleMethod;
import boomerang.scope.soot.jimple.JimplePhantomMethod;
import boomerang.scope.soot.jimple.JimpleStatement;
import boomerang.scope.soot.jimple.JimpleType;
import boomerang.scope.soot.jimple.JimpleVal;
import boomerang.scope.soot.jimple.JimpleWrappedClass;
import soot.SootClass;
import soot.SootFieldRef;
import soot.SootMethod;
import soot.SootMethodRef;
import soot.Value;
import soot.jimple.Stmt;

public class SootScopeConverter {

  public static SootFieldRef toSootFieldRef(Field field) {
    if (field instanceof JimpleField) {
      return ((JimpleField) field).getDelegate();
    }

    throw new RuntimeException("Field is not a JimpleField");
  }

  public static SootMethod toSootMethod(Method method) {
    if (method instanceof JimpleMethod) {
      return ((JimpleMethod) method).getDelegate();
    }

    throw new RuntimeException("Method is not a JimpleMethod");
  }

  public static SootMethodRef toSootMethodRef(PhantomMethod method) {
    if (method instanceof JimplePhantomMethod) {
      return ((JimplePhantomMethod) method).getDelegate();
    }

    throw new RuntimeException("PhantomMethod is not a JimplePhantomMethod");
  }

  public static Stmt toSootStatement(Statement statement) {
    if (statement instanceof JimpleStatement) {
      return ((JimpleStatement) statement).getDelegate();
    }

    throw new RuntimeException("Statement is not a JimpleStatement");
  }

  public static soot.Type toSootType(Type type) {
    if (type instanceof JimpleType) {
      return ((JimpleType) type).getDelegate();
    }

    throw new RuntimeException("Type is not a JimpleType");
  }

  public static Value toSootValue(Val val) {
    if (val instanceof JimpleVal) {
      return ((JimpleVal) val).getDelegate();
    }

    throw new RuntimeException("Val is not a JimpleVal");
  }

  public static SootClass toSootClass(WrappedClass wrappedClass) {
    if (wrappedClass instanceof JimpleWrappedClass) {
      return ((JimpleWrappedClass) wrappedClass).getDelegate();
    }

    throw new RuntimeException("WrappedClass is not a JimpleWrappedClass");
  }
}
