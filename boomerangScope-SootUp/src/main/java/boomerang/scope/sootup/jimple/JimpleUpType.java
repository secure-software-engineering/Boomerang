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
package boomerang.scope.sootup.jimple;

import boomerang.scope.Type;
import boomerang.scope.Val;
import boomerang.scope.WrappedClass;
import java.util.Objects;
import sootup.core.typehierarchy.TypeHierarchy;
import sootup.core.types.ArrayType;
import sootup.core.types.ClassType;
import sootup.core.types.NullType;
import sootup.core.types.PrimitiveType;
import sootup.core.types.ReferenceType;
import sootup.java.core.types.JavaClassType;
import sootup.java.core.views.JavaView;

public class JimpleUpType implements Type {

  private final sootup.core.types.Type delegate;
  private final JavaView view;

  public JimpleUpType(sootup.core.types.Type delegate, JavaView view) {
    this.delegate = delegate;
    this.view = view;
  }

  public sootup.core.types.Type getDelegate() {
    return delegate;
  }

  @Override
  public boolean isNullType() {
    return delegate instanceof NullType;
  }

  @Override
  public boolean isRefType() {
    return delegate instanceof ReferenceType;
  }

  @Override
  public boolean isArrayType() {
    return delegate instanceof ArrayType;
  }

  @Override
  public Type getArrayBaseType() {
    if (isArrayType()) {
      ArrayType arrayType = (ArrayType) delegate;

      return new JimpleUpType(arrayType.getBaseType(), view);
    }

    throw new RuntimeException("Type is not an array type: " + delegate);
  }

  @Override
  public WrappedClass getWrappedClass() {
    if (isRefType()) {
      return new JimpleUpWrappedClass((ClassType) delegate, view);
    }

    throw new RuntimeException("Class of non reference type not available");
  }

  @Override
  public boolean doesCastFail(Type targetVal, Val target) {
    // TODO Requires revisit as it cannot handle NullTypes
    return false;
    /*ClassType targetType = (ClassType) ((JimpleUpType) targetVal).getDelegate();
    if (this.getDelegate() instanceof NullType) {
      return true;
    }

    JavaClassType sourceType = (JavaClassType) this.getDelegate();
    Optional<JavaSootClass> sourceClass = view.getClass(sourceType);
    Optional<JavaSootClass> targetClass = view.getClass(targetType);

    if (sourceClass.isEmpty() || targetClass.isEmpty()) {
      return false;
    }

    if (target instanceof AllocVal && ((AllocVal) target).getAllocVal().isNewExpr()) {
      boolean castFails = view.getTypeHierarchy().isSubtype(targetType, sourceType);
      return !castFails;
    }
    // TODO this line is necessary as canStoreType does not properly work for
    // interfaces, see Java doc.
    if (targetClass.get().isInterface()) {
      return false;
    }
    boolean castFails =
        view.getTypeHierarchy().isSubtype(targetType, sourceType)
            || view.getTypeHierarchy().isSubtype(sourceType, targetType);
    return !castFails;*/
  }

  @Override
  public boolean isSubtypeOf(String type) {
    if (delegate.toString().equals(type)) return true;
    if (!(delegate instanceof ReferenceType)) {
      if (delegate instanceof PrimitiveType) {
        return type.equals(delegate.toString());
      }
      return false;
    }

    if (delegate instanceof NullType) {
      return false;
    }

    if (delegate instanceof ArrayType) {
      // Java treats array types as references of the object class
      return type.equals("java.lang.Object");
    }

    if (!(delegate instanceof ClassType)) {
      return false;
    }

    JavaClassType superType = view.getIdentifierFactory().getClassType(type);

    TypeHierarchy hierarchy = view.getTypeHierarchy();
    if (!hierarchy.contains((ClassType) delegate) || !hierarchy.contains(superType)) {
      return false;
    }

    return hierarchy.isSubtype(superType, delegate);
  }

  @Override
  public boolean isSupertypeOf(String subTypeStr) {
    if (!(delegate instanceof ReferenceType)) {
      if (delegate instanceof PrimitiveType) {
        return subTypeStr.equals(delegate.toString());
      }
      return false;
    }

    if (delegate instanceof NullType) {
      return false;
    }

    if (delegate instanceof ArrayType) {
      // Java treats array types as references of the object class
      return subTypeStr.equals("java.lang.Object");
    }

    if (!(delegate instanceof ClassType)) {
      return false;
    }

    JavaClassType superType = view.getIdentifierFactory().getClassType(subTypeStr);

    TypeHierarchy hierarchy = view.getTypeHierarchy();
    if (!hierarchy.contains((ClassType) delegate) || !hierarchy.contains(superType)) {
      return false;
    }

    return hierarchy.isSubtype(delegate, superType);
  }

  @Override
  public boolean isBooleanType() {
    return delegate instanceof PrimitiveType.BooleanType;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    JimpleUpType that = (JimpleUpType) o;
    return Objects.equals(delegate, that.delegate);
  }

  @Override
  public int hashCode() {
    return Objects.hash(delegate);
  }

  @Override
  public String toString() {
    return this.delegate.toString();
  }
}
