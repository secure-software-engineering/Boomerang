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

import boomerang.scope.DefinedMethod;
import boomerang.scope.Field;
import boomerang.scope.PhantomMethod;
import boomerang.scope.Statement;
import boomerang.scope.Type;
import boomerang.scope.Val;
import boomerang.scope.WrappedClass;
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
import sootup.java.core.JavaSootMethod;

public class SootUpScopeConverter {

  public static FieldSignature toSootUpFieldSignature(Field field) {
    if (field instanceof JimpleUpField) {
      return ((JimpleUpField) field).getDelegate();
    }

    throw new RuntimeException("Field is not a JimpleUpField");
  }

  public static JavaSootMethod toSootUpMethod(DefinedMethod method) {
    if (method instanceof JimpleUpMethod) {
      return ((JimpleUpMethod) method).getDelegate();
    }

    throw new RuntimeException("Method is not a JimpleMethod");
  }

  public static MethodSignature toSootUpMethodSignature(PhantomMethod method) {
    if (method instanceof JimpleUpPhantomMethod) {
      return ((JimpleUpPhantomMethod) method).getDelegate();
    }

    throw new RuntimeException("PhantomMethod is not a JimplePhantomMethod");
  }

  public static Stmt toSootUpStatement(Statement statement) {
    if (statement instanceof JimpleUpStatement) {
      return ((JimpleUpStatement) statement).getDelegate();
    }

    throw new RuntimeException("Statement is not a JimpleStatement");
  }

  public static sootup.core.types.Type toSootUpType(Type type) {
    if (type instanceof JimpleUpType) {
      return ((JimpleUpType) type).getDelegate();
    }

    throw new RuntimeException("Type is not a JimpleType");
  }

  public static Value toSootUpValue(Val val) {
    if (val instanceof JimpleUpVal) {
      return ((JimpleUpVal) val).getDelegate();
    }

    throw new RuntimeException("Val is not a JimpleVal");
  }

  public static ClassType toSootUpClass(WrappedClass wrappedClass) {
    if (wrappedClass instanceof JimpleUpWrappedClass) {
      return ((JimpleUpWrappedClass) wrappedClass).getDelegate();
    }

    throw new RuntimeException("WrappedClass is not a JimpleWrappedClass");
  }
}
