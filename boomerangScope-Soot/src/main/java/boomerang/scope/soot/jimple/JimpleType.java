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
package boomerang.scope.soot.jimple;

import boomerang.scope.AllocVal;
import boomerang.scope.Type;
import boomerang.scope.Val;
import boomerang.scope.WrappedClass;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import soot.ArrayType;
import soot.BooleanType;
import soot.NullType;
import soot.PrimType;
import soot.RefType;
import soot.Scene;
import soot.SootClass;

public class JimpleType implements Type {

  private final soot.Type delegate;
  private final Scene scene;

  public JimpleType(soot.Type type, Scene scene) {
    this.delegate = type;
    this.scene = scene;
  }

  public soot.Type getDelegate() {
    return delegate;
  }

  @Override
  public boolean isNullType() {
    return delegate instanceof NullType;
  }

  @Override
  public boolean isRefType() {
    return delegate instanceof RefType;
  }

  @Override
  public boolean isBooleanType() {
    return delegate instanceof BooleanType;
  }

  @Override
  public boolean isArrayType() {
    return delegate instanceof ArrayType;
  }

  @Override
  public Type getArrayBaseType() {
    if (isArrayType()) {
      ArrayType arrayType = (ArrayType) delegate;

      return new JimpleType(arrayType.baseType, scene);
    }

    throw new RuntimeException("Type is not an array type: " + delegate);
  }

  @Override
  public WrappedClass getWrappedClass() {
    if (isRefType()) {
      return new JimpleWrappedClass(((RefType) delegate).getSootClass(), scene);
    }

    throw new RuntimeException("Class of non reference type is not available");
  }

  @Override
  public boolean doesCastFail(Type targetVal, Val target) {
    RefType targetType = (RefType) ((JimpleType) targetVal).getDelegate();
    RefType sourceType = (RefType) this.getDelegate();
    if (targetType.getSootClass().isPhantom() || sourceType.getSootClass().isPhantom())
      return false;
    if (target instanceof AllocVal && ((AllocVal) target).getAllocVal().isNewExpr()) {
      boolean castFails = scene.getOrMakeFastHierarchy().canStoreType(targetType, sourceType);
      return !castFails;
    }
    // TODO this line is necessary as canStoreType does not properly work for
    // interfaces, see Java doc.
    if (targetType.getSootClass().isInterface()) {
      return false;
    }
    boolean castFails =
        scene.getOrMakeFastHierarchy().canStoreType(targetType, sourceType)
            || scene.getOrMakeFastHierarchy().canStoreType(sourceType, targetType);
    return !castFails;
  }

  // TODO Use FullHierarchy
  public boolean isSubtypeOf(String type) {
    SootClass interfaceType = scene.getSootClass(type);
    if (delegate.toString().equals(type)) return true;
    if (!(delegate instanceof RefType)) {
      if (delegate instanceof ArrayType) {
        return true;
      }
      if (delegate instanceof PrimType) {
        return type.equals(delegate.toString());
      }
      throw new RuntimeException("More");
    }

    RefType allocatedType = (RefType) delegate;
    if (!interfaceType.isInterface()) {
      return scene.getOrMakeFastHierarchy().isSubclass(allocatedType.getSootClass(), interfaceType);
    }
    if (scene
        .getActiveHierarchy()
        .getSubinterfacesOfIncluding(interfaceType)
        .contains(allocatedType.getSootClass())) return true;
    return scene
        .getActiveHierarchy()
        .getImplementersOf(interfaceType)
        .contains(allocatedType.getSootClass());
  }

  @Override
  public boolean isSupertypeOf(String subType) {
    if (!(delegate instanceof RefType)) {
      if (delegate instanceof PrimType) {
        return subType.equals(delegate.toString());
      }
      return false;
    }

    if (!scene.containsClass(subType)) {
      return false;
    }

    RefType thisType = (RefType) delegate;
    if (!thisType.hasSootClass()) {
      return false;
    }

    SootClass thisClass = thisType.getSootClass();
    SootClass subClass = scene.getSootClass(subType);

    Collection<SootClass> hierarchy = getFullHierarchy(subClass, new HashSet<>());
    return hierarchy.contains(thisClass);
  }

  // TODO Move to SootUtils
  private Collection<SootClass> getFullHierarchy(SootClass sourceClass, Set<SootClass> visited) {
    Set<SootClass> result = new HashSet<>();

    if (visited.contains(sourceClass)) {
      return result;
    }

    result.add(sourceClass);
    visited.add(sourceClass);

    // Super interfaces
    Collection<SootClass> interfaces = sourceClass.getInterfaces();
    for (SootClass intFace : interfaces) {
      result.addAll(getFullHierarchy(intFace, visited));
    }

    if (sourceClass.isInterface()) {
      // Super interfaces
      Collection<SootClass> superInterfaces =
          scene.getActiveHierarchy().getSuperinterfacesOf(sourceClass);

      for (SootClass superInterface : superInterfaces) {
        result.addAll(getFullHierarchy(superInterface, visited));
      }
    } else {
      // Super classes
      Collection<SootClass> superClasses =
          scene.getActiveHierarchy().getSuperclassesOf(sourceClass);

      for (SootClass superClass : superClasses) {
        result.addAll(getFullHierarchy(superClass, visited));
      }
    }

    return result;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    JimpleType that = (JimpleType) o;
    return Objects.equals(delegate, that.delegate);
  }

  @Override
  public int hashCode() {
    return Objects.hash(delegate);
  }

  @Override
  public String toString() {
    return delegate.toString();
  }
}
