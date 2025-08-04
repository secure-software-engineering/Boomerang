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

import boomerang.scope.DefinedMethod;
import boomerang.scope.Field;
import boomerang.scope.Method;
import boomerang.scope.PhantomMethod;
import boomerang.scope.Statement;
import boomerang.scope.Val;
import boomerang.scope.WrappedClass;
import boomerang.scope.soot.jimple.JimpleField;
import boomerang.scope.soot.jimple.JimpleMethod;
import boomerang.scope.soot.jimple.JimplePhantomMethod;
import boomerang.scope.soot.jimple.JimpleStatement;
import boomerang.scope.soot.jimple.JimpleType;
import boomerang.scope.soot.jimple.JimpleVal;
import boomerang.scope.soot.jimple.JimpleWrappedClass;
import org.jspecify.annotations.NonNull;
import soot.Scene;
import soot.SootClass;
import soot.SootFieldRef;
import soot.SootMethod;
import soot.SootMethodRef;
import soot.Type;
import soot.Value;
import soot.jimple.Stmt;

/**
 * Converter to create Boomerang scope objects from Soot objects and extract Soot objects from
 * Boomerang scope objects.
 */
public class SootScopeConverter {

  /**
   * Create a {@link JimpleField} from a {@link SootFieldRef}. The returned object represents a
   * field in Boomerang's Soot scope.
   *
   * @param fieldRef the field ref that provides the field information
   * @return the field object in Boomerang's Soot scope
   */
  public static JimpleField createJimpleField(@NonNull SootFieldRef fieldRef) {
    return new JimpleField(fieldRef);
  }

  /**
   * Extract the delegated {@link SootFieldRef} from a {@link Field}. This requires the field to be
   * a {@link JimpleField}.
   *
   * @param field the {@link JimpleField} to extract the {@link SootFieldRef} from
   * @return the delegated {@link SootFieldRef}
   */
  public static SootFieldRef extractSootFieldRef(@NonNull Field field) {
    if (field instanceof JimpleField) {
      return ((JimpleField) field).getDelegate();
    }

    throw new RuntimeException("Field is not a JimpleField");
  }

  /**
   * Create a {@link JimpleMethod} from a {@link SootMethod}. The returned object represents a
   * method in Boomerang's Soot scope. The method is expected to have an existing Jimple body.
   *
   * @param method the method with an existing Jimple body
   * @param scene the scene containing the method
   * @return the method object in Boomerang's Soot scope
   */
  public static JimpleMethod createJimpleMethod(@NonNull SootMethod method, @NonNull Scene scene) {
    return JimpleMethod.of(method, scene);
  }

  /**
   * Extract the delegated {@link SootMethod} from a {@link DefinedMethod}. This requires the method
   * to be a {@link JimpleMethod}.
   *
   * @param method the {@link JimpleMethod} to extract the {@link SootMethod} from
   * @return the delegated {@link SootMethod}
   */
  public static SootMethod extractSootMethod(@NonNull Method method) {
    if (method instanceof JimpleMethod) {
      return ((JimpleMethod) method).getDelegate();
    }

    throw new RuntimeException("Method is not a JimpleMethod");
  }

  /**
   * Create a {@link JimplePhantomMethod} from a {@link SootMethodRef}. The returned object
   * represents a method without an existing Jimple body (phantom method) in Boomerang's Soot scope.
   *
   * @param methodRef the method ref that provides relevant method information
   * @param scene the scene containing the corresponding method
   * @return the method object in Boomerang's Soot scope
   */
  public static JimplePhantomMethod createJimplePhantomMethod(
      @NonNull SootMethodRef methodRef, @NonNull Scene scene) {
    return JimplePhantomMethod.of(methodRef, scene);
  }

  /**
   * Extract the delegated {@link SootMethodRef} from a {@link PhantomMethod}. This requires the
   * method to be a {@link JimplePhantomMethod}.
   *
   * @param method the {@link JimplePhantomMethod} to extract the {@link SootMethodRef} from
   * @return the delegated {@link SootMethodRef}
   */
  public static SootMethodRef extractSootMethodRef(@NonNull PhantomMethod method) {
    if (method instanceof JimplePhantomMethod) {
      return ((JimplePhantomMethod) method).getDelegate();
    }

    throw new RuntimeException("PhantomMethod is not a JimplePhantomMethod");
  }

  /**
   * Create a {@link JimpleStatement} from a {@link Stmt}. The returned object represents a
   * statement in Boomerang's Soot scope.
   *
   * @param stmt the statement to convert
   * @param method the method that contains the statement
   * @return the statement object in Boomerang's Soot scope
   */
  public static JimpleStatement createJimpleStatement(
      @NonNull Stmt stmt, @NonNull JimpleMethod method) {
    return JimpleStatement.create(stmt, method);
  }

  /**
   * Extract the delegated {@link Stmt} from a {@link Statement}. This requires the statement to be
   * a {@link JimpleStatement}.
   *
   * @param statement the {@link JimpleStatement} to extract the {@link Stmt} from
   * @return the delegated {@link Stmt}
   */
  public static Stmt extractSootStatement(@NonNull Statement statement) {
    if (statement instanceof JimpleStatement) {
      return ((JimpleStatement) statement).getDelegate();
    }

    throw new RuntimeException("Statement is not a JimpleStatement");
  }

  /**
   * Create a {@link JimpleType} from a {@link Type}. The returned object represents a type in
   * Boomerang's Soot scope.
   *
   * @param type the type to convert
   * @param scene the scene to resolve type hierarchy information
   * @return the type object in Boomerang's Soot scope
   */
  public static JimpleType createJimpleType(@NonNull Type type, @NonNull Scene scene) {
    return new JimpleType(type, scene);
  }

  /**
   * Extract the delegated {@link Type} from a {@link boomerang.scope.Type}. This requires the type
   * to be a {@link JimpleType}.
   *
   * @param type the {@link JimpleType} to extract the {@link Type} from
   * @return the delegated {@link Type}
   */
  public static Type extractSootType(boomerang.scope.@NonNull Type type) {
    if (type instanceof JimpleType) {
      return ((JimpleType) type).getDelegate();
    }

    throw new RuntimeException("Type is not a JimpleType");
  }

  /**
   * Create a {@link JimpleVal} from a {@link Value}. The returned object represents an arbitrary
   * expression in Boomerang's Soot scope.
   *
   * @param value the value to convert
   * @param method the method that belongs to the value
   * @return the val object in Boomerang's SootUp scope
   */
  public static JimpleVal createJimpleVal(@NonNull Value value, @NonNull JimpleMethod method) {
    return new JimpleVal(value, method);
  }

  /**
   * Extract the delegated {@link Value} from a {@link Val}. This requires the type to be a {@link
   * JimpleVal}.
   *
   * @param val the {@link JimpleVal} to extract the {@link Value} from
   * @return the delegated {@link Value}
   */
  public static Value extractSootValue(@NonNull Val val) {
    if (val instanceof JimpleVal) {
      return ((JimpleVal) val).getDelegate();
    }

    throw new RuntimeException("Val is not a JimpleVal");
  }

  /**
   * Create a {@link JimpleWrappedClass} from a {@link SootClass}. The returned object represents a
   * class in Boomerang's Soot scope.
   *
   * @param sootClass the class containing relevant class information
   * @param scene the scene containing the class type
   * @return the class object in Boomerang's Soot scope
   */
  public static JimpleWrappedClass createJimpleWrappedClass(
      @NonNull SootClass sootClass, @NonNull Scene scene) {
    return new JimpleWrappedClass(sootClass, scene);
  }

  /**
   * Extract the delegated {@link SootClass} from a {@link WrappedClass}. This requires the type to
   * be a {@link JimpleWrappedClass}.
   *
   * @param wrappedClass the {@link JimpleWrappedClass} to extract the {@link SootClass} from
   * @return the delegated {@link SootClass}
   */
  public static SootClass extractSootClass(@NonNull WrappedClass wrappedClass) {
    if (wrappedClass instanceof JimpleWrappedClass) {
      return ((JimpleWrappedClass) wrappedClass).getDelegate();
    }

    throw new RuntimeException("WrappedClass is not a JimpleWrappedClass");
  }
}
