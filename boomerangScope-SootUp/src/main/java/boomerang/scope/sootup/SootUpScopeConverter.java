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
import boomerang.scope.Val;
import boomerang.scope.WrappedClass;
import boomerang.scope.sootup.jimple.JimpleUpField;
import boomerang.scope.sootup.jimple.JimpleUpMethod;
import boomerang.scope.sootup.jimple.JimpleUpPhantomMethod;
import boomerang.scope.sootup.jimple.JimpleUpStatement;
import boomerang.scope.sootup.jimple.JimpleUpType;
import boomerang.scope.sootup.jimple.JimpleUpVal;
import boomerang.scope.sootup.jimple.JimpleUpWrappedClass;
import org.jspecify.annotations.NonNull;
import sootup.core.jimple.basic.Value;
import sootup.core.jimple.common.stmt.Stmt;
import sootup.core.signatures.FieldSignature;
import sootup.core.signatures.MethodSignature;
import sootup.core.types.ClassType;
import sootup.core.types.Type;
import sootup.java.core.JavaSootMethod;
import sootup.java.core.views.JavaView;

/**
 * Converter to create Boomerang scope objects from SootUp objects and extract SootUp objects from
 * Boomerang scope objects.
 */
public class SootUpScopeConverter {

  /**
   * Create a {@link JimpleUpField} from a {@link FieldSignature}. The returned object represents a
   * field in Boomerang's SootUp scope.
   *
   * @param signature the signature that provides the field information
   * @return the field object in Boomerang's SootUp scope
   */
  public static JimpleUpField createJimpleUpField(@NonNull FieldSignature signature) {
    return new JimpleUpField(signature);
  }

  /**
   * Extract the delegated {@link FieldSignature} from a {@link Field}. This requires the field to
   * be a {@link JimpleUpField}.
   *
   * @param field the {@link JimpleUpField} to extract the {@link FieldSignature} from
   * @return the delegated {@link FieldSignature}
   */
  public static FieldSignature extractSootUpFieldSignature(@NonNull Field field) {
    if (field instanceof JimpleUpField) {
      return ((JimpleUpField) field).getDelegate();
    }

    throw new RuntimeException("Field is not a JimpleUpField");
  }

  /**
   * Create a {@link JimpleUpMethod} from a {@link JavaSootMethod}. The returned object represents a
   * method in Boomerang's SootUp scope. The method is expected to have an existing Jimple body.
   *
   * @param method the method with an existing Jimple body
   * @param view the view containing the method
   * @return the method object in Boomerang's SootUp scope
   */
  public static JimpleUpMethod createJimpleUpMethod(
      @NonNull JavaSootMethod method, @NonNull JavaView view) {
    return JimpleUpMethod.of(method, view);
  }

  /**
   * Extract the delegated {@link JavaSootMethod} from a {@link DefinedMethod}. This requires the
   * method to be a {@link JimpleUpMethod}.
   *
   * @param method the {@link JimpleUpMethod} to extract the {@link JavaSootMethod} from
   * @return the delegated {@link JavaSootMethod}
   */
  public static JavaSootMethod extractSootUpMethod(@NonNull DefinedMethod method) {
    if (method instanceof JimpleUpMethod) {
      return ((JimpleUpMethod) method).getDelegate();
    }

    throw new RuntimeException("Method is not a JimpleMethod");
  }

  /**
   * Create a {@link JimpleUpPhantomMethod} from a {@link MethodSignature}. The returned object
   * represents a method without an existing Jimple body (phantom method) in Boomerang's SootUp
   * scope.
   *
   * @param signature the method signature that provides relevant method information
   * @param view the view containing the corresponding method
   * @param isStatic whether the method is static
   * @return the method object in Boomerang's SootUp scope
   */
  public static JimpleUpPhantomMethod createJimpleUpPhantomMethod(
      @NonNull MethodSignature signature, @NonNull JavaView view, @NonNull boolean isStatic) {
    return JimpleUpPhantomMethod.of(signature, view, isStatic);
  }

  /**
   * Extract the delegated {@link MethodSignature} from a {@link PhantomMethod}. This requires the
   * method to be a {@link JimpleUpPhantomMethod}.
   *
   * @param method the {@link JimpleUpPhantomMethod} to extract the {@link MethodSignature} from
   * @return the delegated {@link MethodSignature}
   */
  public static MethodSignature extractSootUpMethodSignature(@NonNull PhantomMethod method) {
    if (method instanceof JimpleUpPhantomMethod) {
      return ((JimpleUpPhantomMethod) method).getDelegate();
    }

    throw new RuntimeException("PhantomMethod is not a JimplePhantomMethod");
  }

  /**
   * Create a {@link JimpleUpStatement} from a {@link Stmt}. The returned object represents a
   * statement in Boomerang's SootUp scope.
   *
   * @param stmt the statement to convert
   * @param method the method that contains the statement
   * @return the statement object in Boomerang's SootUp scope
   */
  public static JimpleUpStatement createJimpleUpStatement(
      @NonNull Stmt stmt, @NonNull JimpleUpMethod method) {
    return JimpleUpStatement.create(stmt, method);
  }

  /**
   * Extract the delegated {@link Stmt} from a {@link Statement}. This requires the statement to be
   * a {@link JimpleUpStatement}.
   *
   * @param statement the {@link JimpleUpStatement} to extract the {@link Stmt} from
   * @return the delegated {@link Stmt}
   */
  public static Stmt extractSootUpStatement(@NonNull Statement statement) {
    if (statement instanceof JimpleUpStatement) {
      return ((JimpleUpStatement) statement).getDelegate();
    }

    throw new RuntimeException("Statement is not a JimpleStatement");
  }

  /**
   * Create a {@link JimpleUpType} from a {@link Type}. The returned object represents a type in
   * Boomerang's SootUp scope.
   *
   * @param type the type to convert
   * @param view the view to resolve type hierarchy information
   * @return the type object in Boomerang's SootUp scope
   */
  public static JimpleUpType createJimpleUpType(@NonNull Type type, @NonNull JavaView view) {
    return new JimpleUpType(type, view);
  }

  /**
   * Extract the delegated {@link Type} from a {@link boomerang.scope.Type}. This requires the type
   * to be a {@link JimpleUpType}.
   *
   * @param type the {@link JimpleUpType} to extract the {@link Type} from
   * @return the delegated {@link Type}
   */
  public static Type extractSootUpType(boomerang.scope.@NonNull Type type) {
    if (type instanceof JimpleUpType) {
      return ((JimpleUpType) type).getDelegate();
    }

    throw new RuntimeException("Type is not a JimpleType");
  }

  /**
   * Create a {@link JimpleUpVal} from a {@link Value}. The returned object represents an arbitrary
   * expression in Boomerang's SootUp scope.
   *
   * @param value the value to convert
   * @param method the method that belongs to the value
   * @return the val object in Boomerang's SootUp scope
   */
  public static JimpleUpVal createJimpleUpVal(
      @NonNull Value value, @NonNull JimpleUpMethod method) {
    return new JimpleUpVal(value, method);
  }

  /**
   * Extract the delegated {@link Value} from a {@link Val}. This requires the type to be a {@link
   * JimpleUpVal}.
   *
   * @param val the {@link JimpleUpVal} to extract the {@link Value} from
   * @return the delegated {@link Value}
   */
  public static Value extractSootUpValue(@NonNull Val val) {
    if (val instanceof JimpleUpVal) {
      return ((JimpleUpVal) val).getDelegate();
    }

    throw new RuntimeException("Val is not a JimpleVal");
  }

  /**
   * Create a {@link JimpleUpWrappedClass} from a {@link ClassType}. The returned object represents
   * a class in Boomerang's SootUp scope.
   *
   * @param classType the class type containing relevant class information
   * @param view the view containing the class type
   * @return the class object in Boomerang's SootUp scope
   */
  public static JimpleUpWrappedClass createJimpleUpWrappedClass(
      @NonNull ClassType classType, @NonNull JavaView view) {
    return new JimpleUpWrappedClass(classType, view);
  }

  /**
   * Extract the delegated {@link ClassType} from a {@link WrappedClass}. This requires the type to
   * be a {@link JimpleUpWrappedClass}.
   *
   * @param wrappedClass the {@link JimpleUpWrappedClass} to extract the {@link ClassType} from
   * @return the delegated {@link ClassType}
   */
  public static ClassType extractSootUpClass(@NonNull WrappedClass wrappedClass) {
    if (wrappedClass instanceof JimpleUpWrappedClass) {
      return ((JimpleUpWrappedClass) wrappedClass).getDelegate();
    }

    throw new RuntimeException("WrappedClass is not a JimpleWrappedClass");
  }
}
