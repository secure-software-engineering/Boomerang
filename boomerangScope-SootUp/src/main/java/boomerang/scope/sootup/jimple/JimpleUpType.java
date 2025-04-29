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

import boomerang.scope.AllocVal;
import boomerang.scope.Type;
import boomerang.scope.Val;
import boomerang.scope.WrappedClass;
import java.util.Objects;
import java.util.Optional;
import sootup.core.types.ArrayType;
import sootup.core.types.ClassType;
import sootup.core.types.NullType;
import sootup.core.types.PrimitiveType;
import sootup.core.types.ReferenceType;
import sootup.java.core.JavaSootClass;
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
    ClassType targetType = (ClassType) ((JimpleUpType) targetVal).getDelegate();
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
    return !castFails;
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

    JavaClassType superType = view.getIdentifierFactory().getClassType(type);
    Optional<JavaSootClass> superClass = view.getClass(superType);

    if (superClass.isEmpty()) {
      return false;
    }

    JavaClassType allocatedType = (JavaClassType) delegate;
    if (!superClass.get().isInterface()) {
      return view.getTypeHierarchy().isSubtype(allocatedType, superClass.get().getType());
    }
    // TODO: [ms] check if seperation of interface/class is necessary
    if (view.getTypeHierarchy()
        .subclassesOf(superClass.get().getType())
        .anyMatch(t -> t == allocatedType)) {
      return true;
    }
    return view.getTypeHierarchy()
        .implementersOf(superClass.get().getType())
        .anyMatch(t -> t == allocatedType);
  }

  @Override
  public boolean isSupertypeOf(String subTypeStr) {
    if (!(delegate instanceof ReferenceType)) {
      if (delegate instanceof PrimitiveType) {
        return subTypeStr.equals(delegate.toString());
      }
      return false;
    }

    JavaClassType subType = view.getIdentifierFactory().getClassType(subTypeStr);
    if (!view.getTypeHierarchy().contains(subType)) {
      return false;
    }

    JavaClassType thisType = view.getIdentifierFactory().getClassType(delegate.toString());
    if (!view.getTypeHierarchy().contains(thisType)) {
      return false;
    }

    return view.getTypeHierarchy().isSubtype(subType, thisType);
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
